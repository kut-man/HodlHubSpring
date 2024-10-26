package com.example.hodlhub.model;

public class Holding {
  private String ticker;
  private String name;
  private double quantity;
  private double averagePurchasePrice;
  private double currentPrice;
  private double totalValue;
  private double oneHourChangePercent;
  private double yesterdayChangePercent;
  private double sevenDaysChangePercent;
  private double plValue;
  private double plPercentValue;

  public Holding() {}

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

  public double getOneHourChangePercent() {
    return oneHourChangePercent;
  }

  public void setOneHourChangePercent(double oneHourChangePercent) {
    this.oneHourChangePercent = oneHourChangePercent;
  }

  public double getYesterdayChangePercent() {
    return yesterdayChangePercent;
  }

  public void setYesterdayChangePercent(double yesterdayChangePercent) {
    this.yesterdayChangePercent = yesterdayChangePercent;
  }

  public double getSevenDaysChangePercent() {
    return sevenDaysChangePercent;
  }

  public void setSevenDaysChangePercent(double sevenDaysChangePercent) {
    this.sevenDaysChangePercent = sevenDaysChangePercent;
  }

  public double getPlValue() {
    return plValue;
  }

  public void setPlValue(double plValue) {
    this.plValue = plValue;
  }

  public double getPlPercentValue() {
    return plPercentValue;
  }

  public void setPlPercentValue(double plPercentValue) {
    this.plPercentValue = plPercentValue;
  }
}
