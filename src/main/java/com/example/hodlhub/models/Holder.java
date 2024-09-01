package com.example.hodlhub.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "Holder")
public class Holder {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "name")
  @NotEmpty
  @Size(min = 2, max = 100, message = "Invalid Username")
  private String name;

  @Column(name = "email")
  @NotEmpty
  @Email(message = "Email should be valid")
  private String email;

  @Column(name = "password_hash")
  @NotEmpty
  private String password;

  @Column(name = "avatar_url")
  private String avatar;

  @OneToMany(mappedBy = "holder")
  private List<Portfolio> portfolioList;

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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public List<Portfolio> getPortfolioList() {
    return portfolioList;
  }

  public void setPortfolioList(List<Portfolio> portfolioList) {
    this.portfolioList = portfolioList;
  }

  @Override
  public String toString() {
    return "Holder{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", email='"
        + email
        + '\''
        + ", password='"
        + password
        + '\''
        + ", avatar='"
        + avatar
        + '\''
        + '}';
  }
}
