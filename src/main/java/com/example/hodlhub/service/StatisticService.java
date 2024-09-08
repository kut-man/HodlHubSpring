package com.example.hodlhub.service;

import com.example.hodlhub.model.Statistic;
import com.example.hodlhub.model.Transaction;
import com.example.hodlhub.util.CoinNetAmountProjection;
import com.example.hodlhub.util.enums.TransactionType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticService {

  public Statistic getStatistics(
      List<Transaction> transactions,
      List<CoinNetAmountProjection> projections,
      Map<String, Map<String, Double>> priceMap) {
    Statistic statistic = new Statistic();
    double totalPlValue =
        calculateRealizedProfit(transactions) + calculateUnrealizedProfit(transactions, projections, priceMap);
    statistic.setTotalPlValue(totalPlValue);
    return statistic;
  }

  private double calculateRealizedProfit(List<Transaction> transactions) {
    double totalRealizedProfit = 0.0;
    Map<String, Double> totalCostBasisPerCoin = new HashMap<>();
    Map<String, Double> totalAmountBoughtPerCoin = new HashMap<>();

    // Track costs and amounts for calculating the cost basis
    for (Transaction transaction : transactions) {
      String ticker = transaction.getCoin().getTicker();

      if (transaction.getTransactionType() == TransactionType.BUY) {
        // Accumulate cost and amount for each coin
        totalCostBasisPerCoin.put(
            ticker,
            totalCostBasisPerCoin.getOrDefault(ticker, 0.0)
                + transaction.getPricePerCoin() * transaction.getAmount());
        totalAmountBoughtPerCoin.put(
            ticker, totalAmountBoughtPerCoin.getOrDefault(ticker, 0.0) + transaction.getAmount());

      } else if (transaction.getTransactionType() == TransactionType.SELL) {
        // Calculate the average cost basis up to the sell point
        double amountSold = transaction.getAmount();
        double sellingPrice = transaction.getPricePerCoin();

        double totalCostBasis = totalCostBasisPerCoin.getOrDefault(ticker, 0.0);
        double totalAmountBought = totalAmountBoughtPerCoin.getOrDefault(ticker, 0.0);

        if (totalAmountBought > 0) {
          double averageCostBasis = totalCostBasis / totalAmountBought;
          double realizedProfit = (sellingPrice * amountSold) - (averageCostBasis * amountSold);
          totalRealizedProfit += realizedProfit;

          // Adjust cost basis and amount after the sale
          totalCostBasisPerCoin.put(ticker, totalCostBasis - (averageCostBasis * amountSold));
          totalAmountBoughtPerCoin.put(ticker, totalAmountBought - amountSold);
        }
      }
    }

    return totalRealizedProfit;
  }

  private double calculateUnrealizedProfit(
      List<Transaction> transactions,
      List<CoinNetAmountProjection> projections,
      Map<String, Map<String, Double>> priceMap) {
    double totalUnrealizedProfit = 0.0;

    for (CoinNetAmountProjection projection : projections) {
      String ticker = projection.getCoinTicker();
      double amountHeld = projection.getNetAmount();

      if (amountHeld > 0) { // Only consider coins that are still held
        double currentMarketPrice = priceMap.get(ticker + "USDT").get("price");
        double averageCostBasis = calculateAverageCostBasis(transactions, ticker);

        double unrealizedProfit =
            (currentMarketPrice * amountHeld) - (averageCostBasis * amountHeld);
        totalUnrealizedProfit += unrealizedProfit;
      }
    }

    return totalUnrealizedProfit;
  }

  private double calculateAverageCostBasis(List<Transaction> transactions, String ticker) {
    double totalCost = 0.0;
    double totalAmountBought = 0.0;

    for (Transaction transaction : transactions) {
      if (transaction.getTransactionType() == TransactionType.BUY
          && transaction.getCoin().getTicker().equals(ticker)) {
        totalCost += transaction.getPricePerCoin() * transaction.getAmount();
        totalAmountBought += transaction.getAmount();
      }
    }

    return totalAmountBought > 0 ? totalCost / totalAmountBought : 0.0;
  }
}
