package com.example.hodlhub.model;

public class Holding {
  private String ticker;
  private String name;
  private double quantity;
  private double averagePurchasePrice;
  private double currentPrice;
  private double totalValue;
  private double pricePercentageChange1h;
  private double pricePercentageChange24h;
  private double pricePercentageChange7d;
  private double profitLoss;

  public Holding() {}

  public Holding(
      String ticker,
      String name,
      double quantity,
      double averagePurchasePrice,
      double currentPrice,
      double totalValue,
      double pricePercentageChange1h,
      double pricePercentageChange24h,
      double pricePercentageChange7d,
      double profitLoss) {
    this.ticker = ticker;
    this.name = name;
    this.quantity = quantity;
    this.averagePurchasePrice = averagePurchasePrice;
    this.currentPrice = currentPrice;
    this.totalValue = totalValue;
    this.pricePercentageChange1h = pricePercentageChange1h;
    this.pricePercentageChange24h = pricePercentageChange24h;
    this.pricePercentageChange7d = pricePercentageChange7d;
    this.profitLoss = profitLoss;
  }

  public String getTicker() {
    return ticker;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getQuantity() {
    return quantity;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  public double getAveragePurchasePrice() {
    return averagePurchasePrice;
  }

  public void setAveragePurchasePrice(double averagePurchasePrice) {
    this.averagePurchasePrice = averagePurchasePrice;
  }

  public double getCurrentPrice() {
    return currentPrice;
  }

  public void setCurrentPrice(double currentPrice) {
    this.currentPrice = currentPrice;
  }

  public double getTotalValue() {
    return totalValue;
  }

  public void setTotalValue(double totalValue) {
    this.totalValue = totalValue;
  }

  public double getPricePercentageChange1h() {
    return pricePercentageChange1h;
  }

  public void setPricePercentageChange1h(double pricePercentageChange1h) {
    this.pricePercentageChange1h = pricePercentageChange1h;
  }

  public double getPricePercentageChange24h() {
    return pricePercentageChange24h;
  }

  public void setPricePercentageChange24h(double pricePercentageChange24h) {
    this.pricePercentageChange24h = pricePercentageChange24h;
  }

  public double getPricePercentageChange7d() {
    return pricePercentageChange7d;
  }

  public void setPricePercentageChange7d(double pricePercentageChange7d) {
    this.pricePercentageChange7d = pricePercentageChange7d;
  }

  public double getProfitLoss() {
    return profitLoss;
  }

  public void setProfitLoss(double profitLoss) {
    this.profitLoss = profitLoss;
  }
}
