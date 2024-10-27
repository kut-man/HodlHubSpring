package com.example.hodlhub.dto.request;

import com.example.hodlhub.util.annotations.ValidTransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RequestTransactionDTO {
  @ValidTransactionType private String transactionType;

  @Min(value = 0, message = "Transaction amount is empty!")
  private double amount;

  @NotNull(message = "Transaction coin is empty!")
  private String coin;

  @Min(value = 0, message = "Transaction price per coin is empty!")
  private float pricePerCoin;

  @Min(value = 0, message = "Transaction portfolio ID is empty!")
  private int portfolioId;

  private String date;

  public String getTransactionType() {
    return transactionType;
  }

  public void setTransactionType(String transactionType) {
    this.transactionType = transactionType;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public String getCoin() {
    return coin;
  }

  public void setCoin(String coin) {
    this.coin = coin;
  }

  public float getPricePerCoin() {
    return pricePerCoin;
  }

  public void setPricePerCoin(float pricePerCoin) {
    this.pricePerCoin = pricePerCoin;
  }

  public int getPortfolioId() {
    return portfolioId;
  }

  public void setPortfolioId(int portfolioId) {
    this.portfolioId = portfolioId;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  @Override
  public String toString() {
    return "RequestTransactionDTO{"
        + "transactionType='"
        + transactionType
        + '\''
        + ", amount="
        + amount
        + ", coin='"
        + coin
        + '\''
        + ", pricePerCoin="
        + pricePerCoin
        + ", portfolioId="
        + portfolioId
        + '}';
  }
}
