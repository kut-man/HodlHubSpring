package com.example.hodlhub.services;

import com.example.hodlhub.models.Holder;
import com.example.hodlhub.models.Portfolio;
import com.example.hodlhub.repositories.HolderRepository;
import com.example.hodlhub.repositories.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final HolderRepository holderRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, HolderRepository holderRepository) {
        this.portfolioRepository = portfolioRepository;
        this.holderRepository = holderRepository;
    }

    public void save(Portfolio portfolio) {
        portfolio.setCreated(new Date());
        portfolioRepository.save(portfolio);
    }

    public List<Portfolio> get(String email) {
        return holderRepository.findByEmail(email).get().getPortfolioList();
    }


    public Portfolio getByIdAndUsername(int portfolioId, int holderId) {
        return portfolioRepository.findByIdAndHolderId(portfolioId, holderId);
    }
}
