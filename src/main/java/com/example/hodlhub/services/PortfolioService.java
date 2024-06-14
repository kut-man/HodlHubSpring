package com.example.hodlhub.services;

import com.example.hodlhub.models.Holder;
import com.example.hodlhub.models.Portfolio;
import com.example.hodlhub.repositories.PortfolioRepository;
import com.example.hodlhub.utils.exceptions.PortfolioNotExistsException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final HolderService holderService;

    public PortfolioService(PortfolioRepository portfolioRepository, HolderService holderService) {
        this.portfolioRepository = portfolioRepository;
        this.holderService = holderService;
    }

    public void save(Portfolio portfolio, String email) {
        Holder holder = holderService.getHolder(email);
        portfolio.setHolder(holder);
        portfolio.setCreated(new Date());
        portfolioRepository.save(portfolio);
    }

    public List<Portfolio> get(String email) {
        return holderService.getHolder(email).getPortfolioList();
    }

    public void removePortfolioByNameAndHolder(int portfolioId, String email){
        Optional<Portfolio> portfolio = portfolioRepository.findById(portfolioId);
        if (portfolio.isPresent() && Objects.equals(portfolio.get().getHolder().getEmail(), email)) {
            portfolioRepository.delete(portfolio.get());
        }
        else {
            throw new PortfolioNotExistsException("/portfolio");
        }
    }

    public Portfolio getByIdAndUsername(int portfolioId, int holderId) {
        return portfolioRepository.findByIdAndHolderId(portfolioId, holderId);
    }
}
