package com.example.hodlhub.services;

import com.example.hodlhub.models.Holder;
import com.example.hodlhub.models.Portfolio;
import com.example.hodlhub.models.Transaction;
import com.example.hodlhub.repositories.PortfolioRepository;
import com.example.hodlhub.repositories.TransactionRepository;
import com.example.hodlhub.utils.CoinNetAmountProjection;
import com.example.hodlhub.utils.enums.TransactionType;
import com.example.hodlhub.utils.exceptions.PortfolioNotExistsException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {
  private final PortfolioRepository portfolioRepository;
  private final HolderService holderService;
  private final ObjectMapper objectMapper;

  private final TransactionRepository transactionRepository;

  public PortfolioService(
      PortfolioRepository portfolioRepository,
      HolderService holderService,
      ObjectMapper objectMapper,
      TransactionRepository transactionRepository) {
    this.portfolioRepository = portfolioRepository;
    this.holderService = holderService;
    this.objectMapper = objectMapper;
    this.transactionRepository = transactionRepository;
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
        portfolio.setTotalValue(0);
        continue;
      }

      List<String> tickers = getTickers(coinNetAmountProjectionList);
      Map<String, Map<String, Double>> priceMap = fetchPricesForTickers(tickers);
      System.out.println(priceMap);

      double totalBalance = calculateTotalBalance(coinNetAmountProjectionList, priceMap);
      portfolio.setTotalValue(totalBalance);

      double realizedProfit = calculateRealizedProfit(portfolio);
      double unrealizedProfit =
          calculateUnrealizedProfit(portfolio, coinNetAmountProjectionList, priceMap);

      portfolio.setTotalProfitLoss(realizedProfit + unrealizedProfit);

      double currentValue = calculateCurrentValue(portfolio, coinNetAmountProjectionList, priceMap);
      double value24HoursAgo =
          calculateValue24HoursAgoAdjusted(portfolio, coinNetAmountProjectionList, priceMap);

      portfolio.setTotalValueChange24h(currentValue - value24HoursAgo);
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

  private List<String> getTickers(List<CoinNetAmountProjection> projections) {
    return projections.stream()
        .map(CoinNetAmountProjection::getCoinTicker)
        .map(ticker -> ticker + "USDT")
        .toList();
  }

  private Map<String, Map<String, Double>> fetchPricesForTickers(List<String> tickers) {
    if (tickers.isEmpty()) return Collections.emptyMap();

    String symbolsParam =
        tickers.stream()
            .map(ticker -> "\"" + ticker + "\"")
            .collect(Collectors.joining(",", "[", "]"));
    String url = "https://testnet.binance.vision/api/v3/ticker/24hr?symbols=" + symbolsParam;

    try {
      RestTemplate restTemplate = new RestTemplate();
      String response = restTemplate.getForObject(url, String.class);
      return parsePricesFromResponse(response);
    } catch (Exception e) {
      e.printStackTrace();
      return Collections.emptyMap();
    }
  }

  private Map<String, Map<String, Double>> parsePricesFromResponse(String response)
      throws IOException {
    List<Map<String, String>> data = objectMapper.readValue(response, new TypeReference<>() {});

    return data.stream()
        .collect(
            Collectors.toMap(
                item -> item.get("symbol"),
                item -> {
                  Map<String, Double> priceData = new HashMap<>();
                  priceData.put("price", Double.valueOf(item.get("lastPrice")));
                  priceData.put("priceChange24hr", Double.valueOf(item.get("priceChange")));
                  return priceData;
                }));
  }

  private double calculateTotalBalance(
      List<CoinNetAmountProjection> projections, Map<String, Map<String, Double>> priceMap) {
    return projections.stream()
        .mapToDouble(
            projection -> {
              String ticker = projection.getCoinTicker() + "USDT";
              Double price = priceMap.get(ticker).get("price");
              return (price != null) ? projection.getNetAmount() * price : 0;
            })
        .sum();
  }

  private double calculateRealizedProfit(Portfolio portfolio) {
    double totalRealizedProfit = 0.0;
    Map<String, Double> totalCostBasisPerCoin = new HashMap<>();
    Map<String, Double> totalAmountBoughtPerCoin = new HashMap<>();

    List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolio.getId());

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
      Portfolio portfolio,
      List<CoinNetAmountProjection> projections,
      Map<String, Map<String, Double>> priceMap) {
    double totalUnrealizedProfit = 0.0;

    for (CoinNetAmountProjection projection : projections) {
      String ticker = projection.getCoinTicker();
      double amountHeld = projection.getNetAmount();

      if (amountHeld > 0) { // Only consider coins that are still held
        double currentMarketPrice = priceMap.get(ticker + "USDT").get("price");
        double averageCostBasis = calculateAverageCostBasis(portfolio, ticker);

        double unrealizedProfit =
            (currentMarketPrice * amountHeld) - (averageCostBasis * amountHeld);
        totalUnrealizedProfit += unrealizedProfit;
      }
    }

    return totalUnrealizedProfit;
  }

  private double calculateAverageCostBasis(Portfolio portfolio, String ticker) {
    List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolio.getId());
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

  private double calculateCurrentValue(
      Portfolio portfolio,
      List<CoinNetAmountProjection> coinNetAmountProjectionList,
      Map<String, Map<String, Double>> priceMap) {
    double currentValue = 0.0;

    // Calculate the current value based on the latest market prices
    for (CoinNetAmountProjection projection : coinNetAmountProjectionList) {
      String ticker = projection.getCoinTicker() + "USDT";
      double currentMarketPrice =
          priceMap.get(ticker).get("price"); // Fetch current price from market data source

      currentValue += projection.getNetAmount() * currentMarketPrice;
    }

    return currentValue;
  }

  private double calculateValue24HoursAgoAdjusted(
      Portfolio portfolio,
      List<CoinNetAmountProjection> coinNetAmountProjectionList,
      Map<String, Map<String, Double>> priceMap) {
    double value24HoursAgo = 0.0;
    Map<String, Double> netAmounts24hAgo = new HashMap<>();

    // Initialize net amounts as of now
    for (CoinNetAmountProjection projection : coinNetAmountProjectionList) {
      netAmounts24hAgo.put(projection.getCoinTicker(), projection.getNetAmount());
    }

    // Adjust net amounts for transactions in the last 24 hours
    List<Transaction> recentTransactions = getTransactionsInLast24Hours(portfolio);

    for (Transaction transaction : recentTransactions) {
      String ticker = transaction.getCoin().getTicker();
      double amount = transaction.getAmount();

      if (transaction.getTransactionType() == TransactionType.BUY) {
        netAmounts24hAgo.put(ticker, netAmounts24hAgo.getOrDefault(ticker, 0.0) - amount);
      } else if (transaction.getTransactionType() == TransactionType.SELL) {
        netAmounts24hAgo.put(ticker, netAmounts24hAgo.getOrDefault(ticker, 0.0) + amount);
      }
    }

    // Calculate the value 24 hours ago using adjusted net amounts and historical prices
    for (Map.Entry<String, Double> entry : netAmounts24hAgo.entrySet()) {
      String ticker = entry.getKey() + "USDT";
      double amountHeld24hAgo = entry.getValue();
      double price24HoursAgo =
          priceMap.get(ticker).get("price") - priceMap.get(ticker).get("priceChange24hr");

      value24HoursAgo += amountHeld24hAgo * price24HoursAgo;
    }

    return value24HoursAgo;
  }

  private List<Transaction> getTransactionsInLast24Hours(Portfolio portfolio) {
    List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolio.getId());
    return transactions.stream()
        .filter(transaction -> transaction.getDate().isAfter(OffsetDateTime.now().minusHours(24)))
        .toList();
  }
}
