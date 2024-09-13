package com.example.hodlhub.service;

import com.example.hodlhub.model.Holding;
import com.example.hodlhub.model.Statistic;
import com.example.hodlhub.model.Transaction;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {

  public Statistic getStatistics(List<Transaction> transactions, List<Holding> holdingList) {
    Statistic statistic = new Statistic();
    double totalPlValue = calculateTotalPlValue(holdingList);
    statistic.setTotalPlValue(totalPlValue);
    statistic.setAllTotalBuySpent(calculateCostBasis(transactions));
    return statistic;
  }

  private double calculateTotalPlValue(List<Holding> holdingList) {
    return holdingList.stream().mapToDouble(Holding::getProfitLoss).sum();
  }

  private double calculateCostBasis(List<Transaction> transactions) {
    double totalCost = 0.0;

    for (Transaction transaction : transactions) {
      double cost = transaction.getAmount() * transaction.getPricePerCoin();
      totalCost += cost;
    }

    return totalCost;
  }
}
