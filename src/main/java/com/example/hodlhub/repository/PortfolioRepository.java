package com.example.hodlhub.repository;

import com.example.hodlhub.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
  Optional<Portfolio> findByIdAndHolderId(long portfolioId, int holderId);
}
