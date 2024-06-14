package com.example.hodlhub.services;

import com.example.hodlhub.dto.request.RequestTransactionDTO;
import com.example.hodlhub.models.Coin;
import com.example.hodlhub.models.Portfolio;
import com.example.hodlhub.models.Transaction;
import com.example.hodlhub.repositories.TransactionRepository;
import com.example.hodlhub.utils.enums.TransactionType;
import com.example.hodlhub.utils.exceptions.CoinNotExistsException;
import com.example.hodlhub.utils.exceptions.PortfolioNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final PortfolioService portfolioService;
    private final CoinService coinService;
    private final HolderService holderService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, PortfolioService portfolioService, CoinService coinService, HolderService holderService) {
        this.transactionRepository = transactionRepository;
        this.portfolioService = portfolioService;
        this.coinService = coinService;
        this.holderService = holderService;
    }

    public Transaction mapToEntity(RequestTransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.valueOf(dto.getTransactionType().toUpperCase()));
        transaction.setAmount(dto.getAmount());
        transaction.setPricePerCoin(dto.getPricePerCoin());

        Coin coin = new Coin();
        coin.setTicker(dto.getCoin());
        transaction.setCoin(coin);

        Portfolio portfolio = new Portfolio();
        portfolio.setId(dto.getPortfolioId());
        transaction.setPortfolio(portfolio);

        transaction.setDate(new Date());

        return transaction;
    }

    public void save(Transaction transaction, String username) {
        Portfolio portfolio = portfolioService.getByIdAndUsername(transaction.getPortfolio().getId(), holderService.getHolder(username).getId());
        if (portfolio == null) throw new PortfolioNotExistsException("/transaction");

        Coin coin = coinService.getCoinByTicker(transaction.getCoin().getTicker());
        if (coin == null) throw new CoinNotExistsException("/transaction");

        transaction.setCoin(coin);
        transactionRepository.save(transaction);
    }
}
