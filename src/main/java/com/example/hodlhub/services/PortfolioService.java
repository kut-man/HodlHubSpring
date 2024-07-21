package com.example.hodlhub.services;

import com.example.hodlhub.models.Holder;
import com.example.hodlhub.models.Portfolio;
import com.example.hodlhub.repositories.PortfolioRepository;
import com.example.hodlhub.repositories.TransactionRepository;
import com.example.hodlhub.utils.CoinNetAmountProjection;
import com.example.hodlhub.utils.exceptions.PortfolioNotExistsException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final HolderService holderService;
    private final ObjectMapper objectMapper;

    private final TransactionRepository transactionRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, HolderService holderService, ObjectMapper objectMapper, TransactionRepository transactionRepository) {
        this.portfolioRepository = portfolioRepository;
        this.holderService = holderService;
        this.objectMapper = objectMapper;
        this.transactionRepository = transactionRepository;
    }

    public void save(Portfolio portfolio, String email) {
        Holder holder = holderService.getHolder(email);
        portfolio.setHolder(holder);
        portfolio.setCreated(new Date());
        portfolioRepository.save(portfolio);
    }

    public List<Portfolio> get(String email) {
        List<String> tickers = transactionRepository.findNetAmountsByPortfolio(8).stream()
                .map(CoinNetAmountProjection::getCoinTicker)
                .map(ticker -> ticker + "USDT")
                .toList();

        String result = tickers.stream()
                .map(ticker -> "\"" + ticker + "\"")
                .collect(Collectors.joining(",", "[", "]"));

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://testnet.binance.vision/api/v3/ticker/price?symbols=" + result;

        String response = restTemplate.getForObject(url, String.class);

        try {
            List<Object> data = objectMapper.readValue(response, new TypeReference<>() {
            });
            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return holderService.getHolder(email).getPortfolioList();
    }

    public void removePortfolioByNameAndHolder(int portfolioId, String email) {
        Optional<Portfolio> portfolio = portfolioRepository.findById(portfolioId);
        if (portfolio.isPresent() && Objects.equals(portfolio.get().getHolder().getEmail(), email)) {
            portfolioRepository.delete(portfolio.get());
        } else {
            throw new PortfolioNotExistsException("/portfolio");
        }
    }

    public Portfolio getByIdAndUsername(int portfolioId, int holderId) {
        return portfolioRepository.findByIdAndHolderId(portfolioId, holderId);
    }
}
