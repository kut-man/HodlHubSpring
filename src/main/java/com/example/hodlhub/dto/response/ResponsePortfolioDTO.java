package com.example.hodlhub.dto.response;

import com.example.hodlhub.model.Holding;
import com.example.hodlhub.model.Statistic;
import java.util.List;

public class ResponsePortfolioDTO {
  private int id;
  private String name;
  private String avatar;
  private String color;
  private double totalAmount;
  private double valueChange24h;
  private double valueChangePercentage24h;
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

  public double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public double getValueChange24h() {
    return valueChange24h;
  }

  public void setValueChange24h(double valueChange24h) {
    this.valueChange24h = valueChange24h;
  }

  public double getValueChangePercentage24h() {
    return valueChangePercentage24h;
  }

  public void setValueChangePercentage24h(double valueChangePercentage24h) {
    this.valueChangePercentage24h = valueChangePercentage24h;
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
