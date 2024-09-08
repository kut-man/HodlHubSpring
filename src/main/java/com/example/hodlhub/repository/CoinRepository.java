package com.example.hodlhub.repository;

import com.example.hodlhub.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoinRepository extends JpaRepository<Coin, Integer> {
  Optional<Coin> findByTicker(String ticker);
}
