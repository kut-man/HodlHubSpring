package com.example.hodlhub.models;

import com.example.hodlhub.utils.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.util.Date;

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
    private long amount;

    @ManyToOne
    @JoinColumn(name = "coin_id", referencedColumnName = "id")
    private Coin coin;

    @Min(value = 0)
    @Column(name = "price_per_unit")
    private long pricePerCoin;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", referencedColumnName = "id")
    private Portfolio portfolio;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

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

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Coin getCoin() {
        return coin;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }

    public long getPricePerCoin() {
        return pricePerCoin;
    }

    public void setPricePerCoin(long pricePerCoin) {
        this.pricePerCoin = pricePerCoin;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
