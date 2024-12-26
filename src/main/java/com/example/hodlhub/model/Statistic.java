package com.example.hodlhub.model;

import java.util.List;

public class Statistic {
  private double totalPlValue;
  private double totalPlPercentValue;
  private double allTotalBuySpent;
  private double bestPlValue;
  private double bestPlPercentValue;
  private String bestName;
  private String bestTicker;
  private double worstPlValue;
  private double worstPlPercentValue;
  private String worstName;
  private String worstTicker;
  private List<AreaChart> areaChart;

  public Statistic() {}

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

  public String getBestName() {
    return bestName;
  }

  public void setBestName(String bestName) {
    this.bestName = bestName;
  }

  public String getBestTicker() {
    return bestTicker;
  }

  public void setBestTicker(String bestTicker) {
    this.bestTicker = bestTicker;
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

  public String getWorstName() {
    return worstName;
  }

  public void setWorstName(String worstName) {
    this.worstName = worstName;
  }

  public String getWorstTicker() {
    return worstTicker;
  }

  public void setWorstTicker(String worstTicker) {
    this.worstTicker = worstTicker;
  }

  public List<AreaChart> getAreaChart() {
    return areaChart;
  }

  public void setAreaChart(List<AreaChart> areaChart) {
    this.areaChart = areaChart;
  }

  public static class AreaChart {

    public AreaChart() {}

  }
}
