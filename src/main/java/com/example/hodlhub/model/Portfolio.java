package com.example.hodlhub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Portfolio")
public class Portfolio {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "name")
  @NotEmpty
  @Size(max = 24, message = "Portfolio name should be less then 24 characters!")
  private String name;

  @Column(name = "avatar")
  @NotEmpty
  private String avatar;

  @Transient private double totalValue;

  @Transient private double totalValueChange24h;

  @Transient private List<Holding> holdings;

  @Transient private Statistic statistics;

  @Column(name = "bg_color")
  @NotEmpty
  private String color;

  @ManyToOne
  @JoinColumn(name = "holder_id", referencedColumnName = "id")
  private Holder holder;

  @Column(name = "created_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  @OneToMany(mappedBy = "portfolio")
  private List<Transaction> transactionList;

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

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public Holder getHolder() {
    return holder;
  }

  public void setHolder(Holder holder) {
    this.holder = holder;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public List<Transaction> getTransactionList() {
    return transactionList;
  }

  public void setTransactionList(List<Transaction> transactionList) {
    this.transactionList = transactionList;
  }
}
