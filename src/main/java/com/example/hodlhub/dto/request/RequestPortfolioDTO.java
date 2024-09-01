package com.example.hodlhub.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RequestPortfolioDTO {
  @NotEmpty(message = "Portfolio name is missing!")
  @Size(max = 24, message = "Portfolio name should be less then 24 characters!")
  private String name;

  @NotEmpty(message = "Avatar is missing!")
  private String avatar;

  @NotEmpty(message = "Icon background color is missing!")
  private String color;

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
}
