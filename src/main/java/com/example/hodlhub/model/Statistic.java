package com.example.hodlhub.model;

import java.util.List;

public class Statistic {
  private double totalPlValue;
  private double totalPlPercentValue;
  private double allTotalBuySpent;
  private double bestPlValue;
  private double bestPlPercentValue;
  private Coin bestCrypto;
  private double worstPlValue;
  private double worstPlPercentValue;
  private Coin worstCoin;
  private List<PieChart> pieCharts;

  // Nested class for PieChart
  public static class PieChart {
    private double holdings;
    private int cryptoId;
    private String name;
    private String symbol;
    private double holdingsPercent;

    // Constructors, Getters, and Setters
    public PieChart() {}

    public double getHoldings() {
      return holdings;
    }

    public void setHoldings(double holdings) {
      this.holdings = holdings;
    }

    public int getCryptoId() {
      return cryptoId;
    }

    public void setCryptoId(int cryptoId) {
      this.cryptoId = cryptoId;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getSymbol() {
      return symbol;
    }

    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    public double getHoldingsPercent() {
      return holdingsPercent;
    }

    public void setHoldingsPercent(double holdingsPercent) {
      this.holdingsPercent = holdingsPercent;
    }
  }

  public Statistic() {}
  ;

  public double getTotalPlValue() {
    return totalPlValue;
  }

  public void setTotalPlValue(double totalPlValue) {
    this.totalPlValue = totalPlValue;
  }

  public double getTotalPlPercentValue() {
    return totalPlPercentValue;
  }

  public void setTotalPlPercentValue(double totalPlPercentValue) {
    this.totalPlPercentValue = totalPlPercentValue;
  }

  public double getAllTotalBuySpent() {
    return allTotalBuySpent;
  }

  public void setAllTotalBuySpent(double allTotalBuySpent) {
    this.allTotalBuySpent = allTotalBuySpent;
  }

  public double getBestPlValue() {
    return bestPlValue;
  }

  public void setBestPlValue(double bestPlValue) {
    this.bestPlValue = bestPlValue;
  }

  public double getBestPlPercentValue() {
    return bestPlPercentValue;
  }

  public void setBestPlPercentValue(double bestPlPercentValue) {
    this.bestPlPercentValue = bestPlPercentValue;
  }

  public Coin getBestCrypto() {
    return bestCrypto;
  }

  public void setBestCrypto(Coin bestCrypto) {
    this.bestCrypto = bestCrypto;
  }

  public double getWorstPlValue() {
    return worstPlValue;
  }

  public void setWorstPlValue(double worstPlValue) {
    this.worstPlValue = worstPlValue;
  }

  public double getWorstPlPercentValue() {
    return worstPlPercentValue;
  }

  public void setWorstPlPercentValue(double worstPlPercentValue) {
    this.worstPlPercentValue = worstPlPercentValue;
  }

  public Coin getWorstCoin() {
    return worstCoin;
  }

  public void setWorstCoin(Coin worstCoin) {
    this.worstCoin = worstCoin;
  }

  public List<PieChart> getPieCharts() {
    return pieCharts;
  }

  public void setPieCharts(List<PieChart> pieCharts) {
    this.pieCharts = pieCharts;
  }
}
