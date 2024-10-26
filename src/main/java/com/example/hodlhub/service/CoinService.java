package com.example.hodlhub.service;

import static com.example.hodlhub.config.BaseCoin.BASE_CURRENCY;
import com.example.hodlhub.model.Coin;
import com.example.hodlhub.repository.CoinRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoinService {

  private final CoinRepository coinRepository;
  private final HoldingService holdingService;

  @Autowired
  public CoinService(CoinRepository coinRepository, HoldingService holdingService) {
    this.coinRepository = coinRepository;
    this.holdingService = holdingService;
  }

  public Coin getCoinByTicker(String ticker) {
    return coinRepository.findByTicker(ticker).orElse(null);
  }

  public List<Coin> getCoins() {
    return coinRepository.findAll();
  }

  public List<Coin> getCoinsWithPrices() {
    List<Coin> coinList = getCoins();
    List<String> tickers = coinList.stream()
            .map(Coin::getTicker)
            .map(ticker -> ticker + BASE_CURRENCY)
            .collect(Collectors.toList());

    Map<String, Double> priceMap = holdingService.fetchPricesForTickers(tickers);

    coinList.forEach(coin -> {
      Double currentPrice = priceMap.get(coin.getTicker() + BASE_CURRENCY);
      coin.setCurrentPrice(currentPrice != null ? currentPrice : 0.0);
    });

    return coinList;
  }
}
