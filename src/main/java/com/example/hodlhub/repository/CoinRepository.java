package com.example.hodlhub.repository;

import com.example.hodlhub.model.Coin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin, Integer> {
  Optional<Coin> findByTicker(String ticker);
}
