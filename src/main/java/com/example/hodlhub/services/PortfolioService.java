package com.example.hodlhub.services;

import com.example.hodlhub.models.Holder;
import com.example.hodlhub.models.Portfolio;
import com.example.hodlhub.repositories.PortfolioRepository;
import com.example.hodlhub.repositories.TransactionRepository;
import com.example.hodlhub.utils.CoinNetAmountProjection;
import com.example.hodlhub.utils.exceptions.PortfolioNotExistsException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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
    RestTemplate restTemplate = new RestTemplate();
    List<Portfolio> portfolioList = holderService.getHolder(email).getPortfolioList();

    for (Portfolio portfolio : portfolioList) {
      List<CoinNetAmountProjection> coinNetAmountProjectionList =
          transactionRepository.findNetAmountsByPortfolio(portfolio.getId());

      if (coinNetAmountProjectionList.isEmpty()) {
        portfolio.setBalance(0);
        continue;
      }

      List<String> tickers = getTickers(coinNetAmountProjectionList);
      Map<String, Double> priceMap = fetchPricesForTickers(restTemplate, tickers);

      double totalBalance = calculateTotalBalance(coinNetAmountProjectionList, priceMap);
      portfolio.setBalance(totalBalance);
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

  private Map<String, Double> fetchPricesForTickers(
      RestTemplate restTemplate, List<String> tickers) {
    if (tickers.isEmpty()) return Collections.emptyMap();

    String symbolsParam =
        tickers.stream()
            .map(ticker -> "\"" + ticker + "\"")
            .collect(Collectors.joining(",", "[", "]"));
    String url = "https://testnet.binance.vision/api/v3/ticker/price?symbols=" + symbolsParam;

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

  private double calculateTotalBalance(
      List<CoinNetAmountProjection> projections, Map<String, Double> priceMap) {
    return projections.stream()
        .mapToDouble(
            projection -> {
              String ticker = projection.getCoinTicker() + "USDT";
              Double price = priceMap.get(ticker);
              return (price != null) ? projection.getNetAmount() * price : 0;
            })
        .sum();
  }
}
