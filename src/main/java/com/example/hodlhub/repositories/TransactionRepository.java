package com.example.hodlhub.repositories;

import com.example.hodlhub.models.Transaction;
import com.example.hodlhub.utils.CoinNetAmountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT c.ticker AS coinTicker, " +
            "SUM(CASE WHEN t.transactionType = 'BUY' THEN t.amount ELSE -t.amount END) AS netAmount " +
            "FROM Transaction t " +
            "JOIN t.coin c " +
            "WHERE t.portfolio.id = :portfolioId " +
            "GROUP BY c.ticker")
    List<CoinNetAmountProjection> findNetAmountsByPortfolio(@Param("portfolioId") int portfolioId);

}
