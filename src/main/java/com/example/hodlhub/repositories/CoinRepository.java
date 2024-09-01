package com.example.hodlhub.repositories;

import com.example.hodlhub.models.Coin;
import com.example.hodlhub.models.Holder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoinRepository extends JpaRepository<Coin, Integer> {
  Optional<Coin> findByTicker(String ticker);
}
