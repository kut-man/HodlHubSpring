package com.example.hodlhub.service;

import static com.example.hodlhub.config.BaseCoin.BASE_CURRENCY;
import com.example.hodlhub.model.Holding;
import com.example.hodlhub.model.Portfolio;
import com.example.hodlhub.model.Transaction;
import com.example.hodlhub.repository.TransactionRepository;
import com.example.hodlhub.util.CoinNetAmountProjection;
import com.example.hodlhub.util.enums.TransactionType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HoldingService {

  private final ObjectMapper objectMapper;
  private final TransactionRepository transactionRepository;

  @Autowired
  public HoldingService(ObjectMapper objectMapper, TransactionRepository transactionRepository) {
    this.objectMapper = objectMapper;
    this.transactionRepository = transactionRepository;
  }

  public List<Holding> getHoldings(Portfolio portfolio) {
    List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolio.getId());
    List<CoinNetAmountProjection> coinNetAmountProjectionList =
        transactionRepository.findNetAmountsByPortfolio(portfolio.getId());

    List<Holding> holdings = new ArrayList<>();

    List<String> tickers = getTickers(coinNetAmountProjectionList);
    Map<String, Double> allCoinPricesMap = fetchPricesForTickers(tickers);

    Map<String, Double> realizedProfit = calculateRealizedProfit(transactions);
    Map<String, Double> unrealizedProfit =
        calculateUnrealizedProfit(transactions, coinNetAmountProjectionList, allCoinPricesMap);

    for (CoinNetAmountProjection projection : coinNetAmountProjectionList) {
      String ticker = projection.getCoinTicker();
      String tradingPair = ticker + BASE_CURRENCY;
      double quantity = projection.getNetAmount();

      if (quantity == 0) {
        continue;
      }

      Map<String, Map<String, Double>> pricesMap = fetchPricesForTicker(tradingPair);
      if (pricesMap.isEmpty()) {
        continue;
      }

      double price7dAgo = pricesMap.get(tradingPair + "_7dAgo").get("closePrice");
      double price1hAgo = pricesMap.get(tradingPair + "_1hAgo").get("openPrice");
      double price24hAgo = pricesMap.get(tradingPair + "_24hAgo").get("closePrice");

      double priceChange1h = ((allCoinPricesMap.get(tradingPair) - price1hAgo) / price1hAgo) * 100;
      double priceChange24h =
          ((allCoinPricesMap.get(tradingPair) - price24hAgo) / price24hAgo) * 100;
      double priceChange7d = ((allCoinPricesMap.get(tradingPair) - price7dAgo) / price7dAgo) * 100;

      double averagePurchasePrice =
          calculateAverageCostBasis(transactions, projection.getCoinTicker());

      double totalValue = quantity * allCoinPricesMap.get(tradingPair);

      double profitLoss =
          realizedProfit.getOrDefault(ticker, 0.0) + unrealizedProfit.getOrDefault(ticker, 0.0);
      double plPercentValue = (profitLoss / (averagePurchasePrice * quantity)) * 100;

      Holding holding = new Holding();
      holding.setTicker(ticker);
      holding.setName(projection.getCoinTicker());
      holding.setQuantity(quantity);
      holding.setAveragePurchasePrice(averagePurchasePrice);
      holding.setCurrentPrice(allCoinPricesMap.get(tradingPair));
      holding.setTotalValue(totalValue);
      holding.setOneHourChangePercent(priceChange1h);
      holding.setYesterdayChangePercent(priceChange24h);
      holding.setSevenDaysChangePercent(priceChange7d);
      holding.setPlValue(profitLoss);
      holding.setPlPercentValue(plPercentValue);

      holdings.add(holding);
    }

    return holdings;
  }

  public Map<String, Map<String, Double>> fetchPricesForTicker(String tradingPair) {
    String url =
        "https://testnet.binance.vision/api/v3/klines?symbol="
            + tradingPair
            + "&interval=1h"
            + "&limit=168";

    try {
      RestTemplate restTemplate = new RestTemplate();
      String response = restTemplate.getForObject(url, String.class);
      return parsePricesFromResponse(response, tradingPair);
    } catch (Exception e) {
      e.printStackTrace();
      return Collections.emptyMap();
    }
  }

  private Map<String, Double> extractPricesFromKline(List<Object> kline) {
    double openPrice = Double.parseDouble((String) kline.get(1));
    double highPrice = Double.parseDouble((String) kline.get(2));
    double lowPrice = Double.parseDouble((String) kline.get(3));
    double closePrice = Double.parseDouble((String) kline.get(4));

    Map<String, Double> tickerPrices = new HashMap<>();
    tickerPrices.put("openPrice", openPrice);
    tickerPrices.put("highPrice", highPrice);
    tickerPrices.put("lowPrice", lowPrice);
    tickerPrices.put("closePrice", closePrice);

    return tickerPrices;
  }

  public Map<String, Double> fetchPricesForTickers(List<String> tradingPair) {
    RestTemplate restTemplate = new RestTemplate();
    if (tradingPair.isEmpty()) return Collections.emptyMap();

    String symbolsParam =
        tradingPair.stream()
            .map(ticker -> "\"" + ticker + "\"")
            .collect(Collectors.joining(",", "[", "]"));
    String url = "https://testnet.binance.vision/api/v3/ticker/price?symbols=" + symbolsParam;
    System.out.println(url);
    try {
      String response = restTemplate.getForObject(url, String.class);
      return parsePricesFromResponse(response);
    } catch (Exception e) {
      e.printStackTrace();
      return Collections.emptyMap();
    }
  }

  private Map<String, Double> parsePricesFromResponse(String response) throws IOException {
    List<Map<String, String>> data = objectMapper.readValue(response, new TypeReference<>() {});
    return data.stream()
        .collect(
            Collectors.toMap(
                item -> item.get("symbol"), item -> Double.valueOf(item.get("price"))));
  }

  private Map<String, Map<String, Double>> parsePricesFromResponse(
      String response, String tradingPair) {
    Map<String, Map<String, Double>> pricesMap = new HashMap<>();

    try {
      List<List<Object>> klineData = objectMapper.readValue(response, new TypeReference<>() {});

      if (!klineData.isEmpty() && klineData.size() >= 167) {
        List<Object> firstHour = klineData.get(0);
        Map<String, Double> firstHourPrices = extractPricesFromKline(firstHour);
        pricesMap.put(tradingPair + "_7dAgo", firstHourPrices);

        List<Object> twentyFourthHour = klineData.get(klineData.size() - 23);
        Map<String, Double> twentyFourthHourPrices = extractPricesFromKline(twentyFourthHour);
        pricesMap.put(tradingPair + "_24hAgo", twentyFourthHourPrices);

        List<Object> lastHour = klineData.get(klineData.size() - 1);
        Map<String, Double> lastHourPrices = extractPricesFromKline(lastHour);
        pricesMap.put(tradingPair + "_1hAgo", lastHourPrices);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return pricesMap;
  }

  private List<String> getTickers(List<CoinNetAmountProjection> projections) {
    return projections.stream()
        .map(CoinNetAmountProjection::getCoinTicker)
        .map(ticker -> ticker + BASE_CURRENCY)
        .toList();
  }

  public Map<String, Double> calculateRealizedProfit(List<Transaction> transactions) {
    Map<String, Double> realizedProfitByCoin = new HashMap<>();
    Map<String, Double> totalCostBasisPerCoin = new HashMap<>();
    Map<String, Double> totalAmountBoughtPerCoin = new HashMap<>();

    for (Transaction transaction : transactions) {
      String ticker = transaction.getCoin().getTicker();

      if (transaction.getTransactionType() == TransactionType.BUY) {
        totalCostBasisPerCoin.put(
            ticker,
            totalCostBasisPerCoin.getOrDefault(ticker, 0.0)
                + transaction.getPricePerCoin() * transaction.getAmount());
        totalAmountBoughtPerCoin.put(
            ticker, totalAmountBoughtPerCoin.getOrDefault(ticker, 0.0) + transaction.getAmount());

      } else if (transaction.getTransactionType() == TransactionType.SELL) {
        double amountSold = transaction.getAmount();
        double sellingPrice = transaction.getPricePerCoin();

        double totalCostBasis = totalCostBasisPerCoin.getOrDefault(ticker, 0.0);
        double totalAmountBought = totalAmountBoughtPerCoin.getOrDefault(ticker, 0.0);

        if (totalAmountBought > 0) {
          double averageCostBasis = totalCostBasis / totalAmountBought;
          double realizedProfit = (sellingPrice * amountSold) - (averageCostBasis * amountSold);
          realizedProfitByCoin.put(
              ticker, realizedProfitByCoin.getOrDefault(ticker, 0.0) + realizedProfit);

          totalCostBasisPerCoin.put(ticker, totalCostBasis - (averageCostBasis * amountSold));
          totalAmountBoughtPerCoin.put(ticker, totalAmountBought - amountSold);
        }
      }
    }

    return realizedProfitByCoin;
  }

  public Map<String, Double> calculateUnrealizedProfit(
      List<Transaction> transactions,
      List<CoinNetAmountProjection> projections,
      Map<String, Double> priceMap) {
    Map<String, Double> unrealizedProfitByCoin = new HashMap<>();

    for (CoinNetAmountProjection projection : projections) {
      String ticker = projection.getCoinTicker();
      double amountHeld = projection.getNetAmount();

      if (amountHeld > 0) {
        double currentMarketPrice = priceMap.get(ticker + BASE_CURRENCY);
        double averageCostBasis = calculateAverageCostBasis(transactions, ticker);

        double unrealizedProfit =
            (currentMarketPrice * amountHeld) - (averageCostBasis * amountHeld);
        unrealizedProfitByCoin.put(ticker, unrealizedProfit);
      }
    }

    return unrealizedProfitByCoin;
  }

  public double calculateAverageCostBasis(List<Transaction> transactions, String ticker) {
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
