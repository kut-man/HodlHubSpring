package com.example.hodlhub.service;

import com.example.hodlhub.dto.request.RequestTransactionDTO;
import com.example.hodlhub.model.Coin;
import com.example.hodlhub.model.Portfolio;
import com.example.hodlhub.model.Transaction;
import com.example.hodlhub.repository.TransactionRepository;
import com.example.hodlhub.util.enums.TransactionType;
import com.example.hodlhub.util.exceptions.CoinNotExistsException;
import com.example.hodlhub.util.exceptions.PortfolioNotExistsException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final PortfolioService portfolioService;
  private final CoinService coinService;
  private final HolderService holderService;

  @Autowired
  public TransactionService(
      TransactionRepository transactionRepository,
      PortfolioService portfolioService,
      CoinService coinService,
      HolderService holderService) {
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

    if (dto.getDate() != null) {
      DateTimeFormatter formatter =
          DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)", Locale.ENGLISH);
      transaction.setDate(OffsetDateTime.parse(dto.getDate(), formatter));
    } else {
      transaction.setDate(OffsetDateTime.now());
    }
    return transaction;
  }

  public void save(Transaction transaction, String username) {
    Portfolio portfolio =
        portfolioService.getByIdAndUsername(
            transaction.getPortfolio().getId(), holderService.getHolder(username).getId());
    if (portfolio == null) throw new PortfolioNotExistsException("/transaction");

    Coin coin = coinService.getCoinByTicker(transaction.getCoin().getTicker());
    if (coin == null) throw new CoinNotExistsException("/transaction");

    transaction.setCoin(coin);
    transactionRepository.save(transaction);
  }
}
