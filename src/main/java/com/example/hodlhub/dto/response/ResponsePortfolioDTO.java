package com.example.hodlhub.dto.response;

import com.example.hodlhub.model.Holding;
import com.example.hodlhub.model.Statistic;

import java.util.List;

public class ResponsePortfolioDTO {
  private int id;

  private String name;

  private String avatar;

  private String color;

  private double totalInvestment;

  private double totalValue;

  private double totalValueChange24h;

  private List<Holding> holdings;

  private Statistic statistics;

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

  public double getTotalValueChange24h() {
    return totalValueChange24h;
  }

  public void setTotalValueChange24h(double totalValueChange24h) {
    this.totalValueChange24h = totalValueChange24h;
  }

  public List<Holding> getHoldings() {
    return holdings;
  }

  public void setHoldings(List<Holding> holdings) {
    this.holdings = holdings;
  }

  public Statistic getStatistics() {
    return statistics;
  }

  public void setStatistics(Statistic statistics) {
    this.statistics = statistics;
  }
}
