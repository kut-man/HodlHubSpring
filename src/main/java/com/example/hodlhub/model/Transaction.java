package com.example.hodlhub.model;

import com.example.hodlhub.util.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.time.OffsetDateTime;

@Entity
@Table(name = "Transaction")
public class Transaction {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "transaction_type")
    private TransactionType transactionType;
    @Min(value = 0, message = "")
    @Column(name = "amount")
    private double amount;

    @ManyToOne
    @JoinColumn(name = "coin_id", referencedColumnName = "id")
    private Coin coin;

    @Min(value = 0)
    @Column(name = "price_per_unit")
    private float pricePerCoin;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", referencedColumnName = "id")
    private Portfolio portfolio;

    @Column(name = "date")
    private OffsetDateTime date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Coin getCoin() {
        return coin;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }

    public float getPricePerCoin() {
        return pricePerCoin;
    }

    public void setPricePerCoin(float pricePerCoin) {
        this.pricePerCoin = pricePerCoin;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", coin=" + coin +
                ", pricePerCoin=" + pricePerCoin +
                ", portfolio=" + portfolio +
                ", date=" + date +
                '}';
    }
}
