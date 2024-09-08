package com.example.hodlhub.repository;

import com.example.hodlhub.model.Transaction;
import com.example.hodlhub.util.CoinNetAmountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
  @Query(
      "SELECT c.ticker AS coinTicker, "
          + "SUM(CASE WHEN t.transactionType = 'BUY' THEN t.amount ELSE -t.amount END) AS netAmount "
          + "FROM Transaction t "
          + "JOIN t.coin c "
          + "WHERE t.portfolio.id = :portfolioId "
          + "GROUP BY c.ticker")
  List<CoinNetAmountProjection> findNetAmountsByPortfolio(@Param("portfolioId") int portfolioId);

  List<Transaction> findByPortfolioId(int portfolioId);
}
