package com.example.hodlhub.services;

import com.example.hodlhub.models.Coin;
import com.example.hodlhub.repositories.CoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
