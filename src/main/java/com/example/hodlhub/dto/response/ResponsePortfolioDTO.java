package com.example.hodlhub.dto.response;

public class ResponsePortfolioDTO {
  private int id;

  private String name;

  private String avatar;

  private String color;

  private double totalInvestment;

  private double totalValue;

  private double totalProfitLoss;

  private double totalValueChange24h;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public double getTotalInvestment() {
    return totalInvestment;
  }

  public void setTotalInvestment(double totalInvestment) {
    this.totalInvestment = totalInvestment;
  }

  public double getTotalValue() {
    return totalValue;
  }

  public void setTotalValue(double totalValue) {
    this.totalValue = totalValue;
  }

  public double getTotalProfitLoss() {
    return totalProfitLoss;
  }

  public void setTotalProfitLoss(double totalProfitLoss) {
    this.totalProfitLoss = totalProfitLoss;
  }

  public double getTotalValueChange24h() {
    return totalValueChange24h;
  }

  public void setTotalValueChange24h(double totalValueChange24h) {
    this.totalValueChange24h = totalValueChange24h;
  }
}
