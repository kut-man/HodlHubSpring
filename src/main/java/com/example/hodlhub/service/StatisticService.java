package com.example.hodlhub.service;

import com.example.hodlhub.model.Holding;
import com.example.hodlhub.model.Statistic;
import com.example.hodlhub.model.Transaction;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class StatisticService {

  public Statistic getStatistics(List<Transaction> transactions, List<Holding> holdingList) {
    Statistic statistic = new Statistic();
    double totalPlValue = calculateTotalPlValue(holdingList);
    statistic.setTotalPlValue(totalPlValue);
    statistic.setAllTotalBuySpent(calculateCostBasis(transactions));

    Holding bestPerformer = findBestPerformer(holdingList).get();
    Holding worstPerformer = findWorstPerformer(holdingList).get();

    statistic.setBestName(bestPerformer.getName());
    statistic.setBestTicker(bestPerformer.getTicker());
    statistic.setBestPlValue(bestPerformer.getPlValue());
    statistic.setBestPlPercentValue(bestPerformer.getPlPercentValue());
    
    statistic.setWorstName(worstPerformer.getName());
    statistic.setWorstTicker(worstPerformer.getTicker());
    statistic.setWorstPlValue(worstPerformer.getPlValue());
    statistic.setWorstPlPercentValue(worstPerformer.getPlPercentValue());
    return statistic;
  }

  private double calculateTotalPlValue(List<Holding> holdingList) {
    return holdingList.stream().mapToDouble(Holding::getPlValue).sum();
  }

  private double calculateCostBasis(List<Transaction> transactions) {
    double totalCost = 0.0;

    for (Transaction transaction : transactions) {
      double cost = transaction.getAmount() * transaction.getPricePerCoin();
      totalCost += cost;
    }

    return totalCost;
  }

  private Optional<Holding> findBestPerformer(List<Holding> holdingList) {
    return holdingList.stream()
            .max(Comparator.comparingDouble(Holding::getPlValue));
  }

  private Optional<Holding> findWorstPerformer(List<Holding> holdingList) {
    return holdingList.stream()
            .min(Comparator.comparingDouble(Holding::getPlValue));
  }
}
