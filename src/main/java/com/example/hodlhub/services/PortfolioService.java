package com.example.hodlhub.services;

import com.example.hodlhub.models.Portfolio;
import com.example.hodlhub.repositories.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public void save(Portfolio portfolio) {
        portfolio.setCreated(new Date());
        portfolioRepository.save(portfolio);
    }
}
