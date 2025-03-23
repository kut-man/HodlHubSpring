package com.example.hodlhub.service;

import static com.example.hodlhub.config.BaseCoin.BASE_CURRENCY;

import com.example.hodlhub.dto.response.ResponseChartDataDTO;
import com.example.hodlhub.model.Holder;
import com.example.hodlhub.model.Portfolio;
import com.example.hodlhub.model.Transaction;
import com.example.hodlhub.repository.TransactionRepository;
import com.example.hodlhub.util.exceptions.PortfolioNotExistsException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioChartService {
  private static final Map<String, TimeFrame> INTERVAL_MAP = new HashMap<>();
  private static final int MAX_DATA_POINTS = 300;

  static {
    INTERVAL_MAP.put("1m", new TimeFrame(1, ChronoUnit.MINUTES));
    INTERVAL_MAP.put("3m", new TimeFrame(3, ChronoUnit.MINUTES));
    INTERVAL_MAP.put("5m", new TimeFrame(5, ChronoUnit.MINUTES));
    INTERVAL_MAP.put("15m", new TimeFrame(15, ChronoUnit.MINUTES));
    INTERVAL_MAP.put("30m", new TimeFrame(30, ChronoUnit.MINUTES));

    INTERVAL_MAP.put("1h", new TimeFrame(1, ChronoUnit.HOURS));
    INTERVAL_MAP.put("2h", new TimeFrame(2, ChronoUnit.HOURS));
    INTERVAL_MAP.put("4h", new TimeFrame(4, ChronoUnit.HOURS));
    INTERVAL_MAP.put("6h", new TimeFrame(6, ChronoUnit.HOURS));
    INTERVAL_MAP.put("8h", new TimeFrame(8, ChronoUnit.HOURS));
    INTERVAL_MAP.put("12h", new TimeFrame(12, ChronoUnit.HOURS));

    INTERVAL_MAP.put("1d", new TimeFrame(1, ChronoUnit.DAYS));
    INTERVAL_MAP.put("3d", new TimeFrame(3, ChronoUnit.DAYS));

    INTERVAL_MAP.put("1w", new TimeFrame(1, ChronoUnit.WEEKS));
  }

  private final TransactionRepository transactionRepository;
  private final HolderService holderService;
  private final PortfolioService portfolioService;
  private final BinanceApiClient binanceApiClient;

  @Autowired
  public PortfolioChartService(
      TransactionRepository transactionRepository,
      HolderService holderService,
      PortfolioService portfolioService,
      BinanceApiClient binanceApiClient) {
    this.transactionRepository = transactionRepository;
    this.holderService = holderService;
    this.portfolioService = portfolioService;
    this.binanceApiClient = binanceApiClient;
  }

  public List<ResponseChartDataDTO> getPortfolioHistoricalValue(
      int portfolioId, String email, String interval) {
    if (!INTERVAL_MAP.containsKey(interval)) {
      throw new IllegalArgumentException("Invalid interval: " + interval);
    }

    Holder holder = holderService.getHolder(email);

    Portfolio portfolio =
        portfolioService
            .getByIdAndUsername(portfolioId, holder.getId())
            .orElseThrow(() -> new PortfolioNotExistsException("/portfolio"));

    List<Transaction> transactions = transactionRepository.findByPortfolioId(portfolio.getId());
    if (transactions.isEmpty()) {
      return Collections.emptyList();
    }

    Set<String> uniqueCoins =
        transactions.stream().map(t -> t.getCoin().getTicker()).collect(Collectors.toSet());

    LocalDateTime endDate = OffsetDateTime.now(ZoneId.systemDefault()).toLocalDateTime();

    TimeFrame timeFrame = INTERVAL_MAP.get(interval);
    LocalDateTime startDate = calculateStartDate(endDate, timeFrame);

    Map<String, TreeMap<LocalDateTime, Double>> historicalPrices = new HashMap<>();
    for (String coin : uniqueCoins) {
      historicalPrices.put(
          coin,
          binanceApiClient.fetchHistoricalPrices(
              coin + BASE_CURRENCY, interval, startDate, endDate));
    }

    List<ResponseChartDataDTO> chartData = new ArrayList<>();
    LocalDateTime currentDate = startDate;

    while (!currentDate.isAfter(endDate)) {
      double portfolioValue =
          calculatePortfolioValueForDate(currentDate, transactions, historicalPrices);

      chartData.add(new ResponseChartDataDTO(currentDate, portfolioValue));
      currentDate = timeFrame.addToDate(currentDate);
    }

    return chartData;
  }

  private LocalDateTime calculateStartDate(LocalDateTime endDate, TimeFrame timeFrame) {
    return endDate.minus(MAX_DATA_POINTS * timeFrame.amount(), timeFrame.unit());
  }

  private double calculatePortfolioValueForDate(
      LocalDateTime date,
      List<Transaction> transactions,
      Map<String, TreeMap<LocalDateTime, Double>> historicalPrices) {

    List<Transaction> relevantTransactions =
        transactions.stream().filter(t -> !t.getDate().toLocalDateTime().isAfter(date)).toList();

    Map<String, Double> holdings = new HashMap<>();
    for (Transaction t : relevantTransactions) {
      String coin = t.getCoin().getTicker();
      double amount = t.getAmount();

      switch (t.getTransactionType()) {
        case BUY:
          holdings.put(coin, holdings.getOrDefault(coin, 0.0) + amount);
          break;
        case SELL:
          holdings.put(coin, holdings.getOrDefault(coin, 0.0) - amount);
          break;
      }
    }

    double totalValue = 0.0;
    for (Map.Entry<String, Double> holding : holdings.entrySet()) {
      String coin = holding.getKey();
      double amount = holding.getValue();

      TreeMap<LocalDateTime, Double> coinPrices = historicalPrices.get(coin);
      Optional<Double> coinPrice = getPriceNearestTo(date, coinPrices);
      if (coinPrice.isPresent()) {
        totalValue += amount * coinPrice.get();
      }
    }

    return totalValue;
  }

  public Optional<Double> getPriceNearestTo(
      LocalDateTime dateTime, TreeMap<LocalDateTime, Double> prices) {
    Double exactPrice = prices.get(dateTime);
    if (exactPrice != null) {
      return Optional.of(exactPrice);
    }

    Map.Entry<LocalDateTime, Double> floorEntry = prices.floorEntry(dateTime);
    if (floorEntry != null) {
      return Optional.of(floorEntry.getValue());
    }

    Map.Entry<LocalDateTime, Double> ceilingEntry = prices.ceilingEntry(dateTime);
    if (ceilingEntry != null) {
      return Optional.of(ceilingEntry.getValue());
    }

    return Optional.empty();
  }

  private record TimeFrame(long amount, ChronoUnit unit) {
    LocalDateTime subtractFromDate(LocalDateTime date) {
      return date.minus(amount, unit);
    }

    LocalDateTime addToDate(LocalDateTime date) {
      return date.plus(amount, unit);
    }
  }
}
