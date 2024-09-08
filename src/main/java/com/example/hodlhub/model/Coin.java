package com.example.hodlhub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "Coin")
public class Coin {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotNull
  @Column(name = "name")
  private String name;

  @NotNull
  @Column(name = "logo_url")
  private String logo;

  @NotNull
  @Column(name = "ticker")
  private String ticker;

  @OneToMany(mappedBy = "coin")
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

  public String getLogo() {
    return logo;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }

  public String getTicker() {
    return ticker;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public List<Transaction> getTransactionList() {
    return transactionList;
  }

  public void setTransactionList(List<Transaction> transactionList) {
    this.transactionList = transactionList;
  }
}
