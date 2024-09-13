package com.example.hodlhub.service;

import com.example.hodlhub.model.Coin;
import com.example.hodlhub.repository.CoinRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoinService {

  private final CoinRepository coinRepository;

  @Autowired
  public CoinService(CoinRepository coinRepository) {
    this.coinRepository = coinRepository;
  }

  public Coin getCoinByTicker(String ticker) {
    return coinRepository.findByTicker(ticker).orElse(null);
  }

  public List<Coin> getCoins() {
    return coinRepository.findAll();
  }
}
