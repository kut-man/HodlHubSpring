package com.example.hodlhub.service;

import com.example.hodlhub.model.Holder;
import com.example.hodlhub.model.Portfolio;
import com.example.hodlhub.model.Transaction;
import com.example.hodlhub.repository.PortfolioRepository;
import com.example.hodlhub.repository.TransactionRepository;
import com.example.hodlhub.util.CoinNetAmountProjection;
import com.example.hodlhub.util.enums.TransactionType;
import com.example.hodlhub.util.exceptions.PortfolioNotExistsException;
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
  private final TransactionRepository transactionRepository;
  private final HolderService holderService;
  private final StatisticService statisticService;
  private final ObjectMapper objectMapper;

  public PortfolioService(
      PortfolioRepository portfolioRepository,
      HolderService holderService,
      ObjectMapper objectMapper,
      TransactionRepository transactionRepository,
      StatisticService statisticService) {
    this.portfolioRepository = portfolioRepository;
    this.holderService = holderService;
    this.objectMapper = objectMapper;
    this.transactionRepository = transactionRepository;
    this.statisticService = statisticService;
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

      double totalBalance = calculateTotalBalance(coinNetAmountProjectionList, priceMap);
      portfolio.setTotalValue(totalBalance);

      double value24HoursAgo =
          calculateValue24HoursAgoAdjusted(portfolio, coinNetAmountProjectionList, priceMap);

      portfolio.setTotalValueChange24h(totalBalance - value24HoursAgo);

      List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolio.getId());
      portfolio.setStatistics(
              statisticService.getStatistics(transactions, coinNetAmountProjectionList, priceMap));
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
