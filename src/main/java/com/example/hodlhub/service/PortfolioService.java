package com.example.hodlhub.service;

import com.example.hodlhub.model.Holder;
import com.example.hodlhub.model.Holding;
import com.example.hodlhub.model.Portfolio;
import com.example.hodlhub.model.Transaction;
import com.example.hodlhub.repository.PortfolioRepository;
import com.example.hodlhub.repository.TransactionRepository;
import com.example.hodlhub.util.CoinNetAmountProjection;
import com.example.hodlhub.util.enums.TransactionType;
import com.example.hodlhub.util.exceptions.PortfolioNotExistsException;
import java.time.OffsetDateTime;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {
  private final PortfolioRepository portfolioRepository;
  private final TransactionRepository transactionRepository;
  private final HolderService holderService;
  private final StatisticService statisticService;
  private final HoldingService holdingService;

  public PortfolioService(
      PortfolioRepository portfolioRepository,
      HolderService holderService,
      TransactionRepository transactionRepository,
      StatisticService statisticService,
      HoldingService holdingService) {
    this.portfolioRepository = portfolioRepository;
    this.holderService = holderService;
    this.transactionRepository = transactionRepository;
    this.statisticService = statisticService;
    this.holdingService = holdingService;
  }

  public void save(Portfolio portfolio, String email) {
    Holder holder = holderService.getHolder(email);
    portfolio.setHolder(holder);
    portfolio.setCreated(new Date());
    portfolioRepository.save(portfolio);
  }

  public List<Portfolio> get(String email) {

    List<Portfolio> portfolioList = holderService.getHolder(email).getPortfolioList();

    for (Portfolio portfolio : portfolioList) {
      List<CoinNetAmountProjection> coinNetAmountProjectionList =
          transactionRepository.findNetAmountsByPortfolio(portfolio.getId());

      if (coinNetAmountProjectionList.isEmpty()) {
        portfolio.setTotalAmount(0);
        continue;
      }

      List<Holding> holdings = holdingService.getHoldings(portfolio);
      portfolio.setHoldings(holdings);

      List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolio.getId());
      portfolio.setStatistics(statisticService.getStatistics(transactions, holdings));

      double totalAmount = calculateTotalAmount(holdings);
      portfolio.setTotalAmount(totalAmount);

      double value24HoursAgo =
          calculateValue24HoursAgoAdjusted(transactions, coinNetAmountProjectionList, holdings);

      portfolio.setValueChange24h(totalAmount - value24HoursAgo);

      double percentageChange24h = 0;
      if (value24HoursAgo != 0) {
        percentageChange24h = ((totalAmount - value24HoursAgo) / value24HoursAgo) * 100;
      }
      portfolio.setValueChangePercentage24h(percentageChange24h);
    }

    return portfolioList;
  }

  public void removePortfolioByNameAndHolder(int portfolioId, String email) {
    Optional<Portfolio> portfolio = portfolioRepository.findById(portfolioId);
    if (portfolio.isPresent() && Objects.equals(portfolio.get().getHolder().getEmail(), email)) {
      portfolioRepository.delete(portfolio.get());
    } else {
      throw new PortfolioNotExistsException("/portfolio");
    }
  }

  public Portfolio getByIdAndUsername(int portfolioId, int holderId) {
    return portfolioRepository.findByIdAndHolderId(portfolioId, holderId);
  }

  private double calculateTotalAmount(List<Holding> holdingList) {
    double totalAmount = 0.0;

    for (Holding holding : holdingList) {
      double quantity = holding.getQuantity();
      double currentPrice = holding.getCurrentPrice();
      totalAmount += quantity * currentPrice;
    }

    return totalAmount;
  }

  private double calculateValue24HoursAgoAdjusted(
      List<Transaction> transactions,
      List<CoinNetAmountProjection> coinNetAmountProjectionList,
      List<Holding> holdings) {
    double value24HoursAgo = 0.0;
    Map<String, Double> netAmounts24hAgo = new HashMap<>();

    for (CoinNetAmountProjection projection : coinNetAmountProjectionList) {
      netAmounts24hAgo.put(projection.getCoinTicker(), projection.getNetAmount());
    }

    List<Transaction> recentTransactions = getTransactionsInLast24Hours(transactions);

    for (Transaction transaction : recentTransactions) {
      String ticker = transaction.getCoin().getTicker();
      double amount = transaction.getAmount();

      if (transaction.getTransactionType() == TransactionType.BUY) {
        netAmounts24hAgo.put(ticker, netAmounts24hAgo.getOrDefault(ticker, 0.0) - amount);
      } else if (transaction.getTransactionType() == TransactionType.SELL) {
        netAmounts24hAgo.put(ticker, netAmounts24hAgo.getOrDefault(ticker, 0.0) + amount);
      }
    }

    for (Holding holding : holdings) {
      double netAmount = netAmounts24hAgo.getOrDefault(holding.getTicker(), 0.0);
      double currentPrice = holding.getCurrentPrice();
      double percentageChange = holding.getYesterdayChangePercent() / 100;
      double historicalPrice = currentPrice / (1 + percentageChange);

      value24HoursAgo += netAmount * historicalPrice;
    }

    return value24HoursAgo;
  }

  private List<Transaction> getTransactionsInLast24Hours(List<Transaction> transactions) {
    return transactions.stream()
        .filter(transaction -> transaction.getDate().isAfter(OffsetDateTime.now().minusHours(24)))
        .toList();
  }
}
