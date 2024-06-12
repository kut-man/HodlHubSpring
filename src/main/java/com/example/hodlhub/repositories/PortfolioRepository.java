package com.example.hodlhub.repositories;

import com.example.hodlhub.models.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
    Portfolio findByIdAndHolderId(long portfolioId, int holderId);
}
