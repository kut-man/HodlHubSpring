package com.example.hodlhub.dto.response;

import java.time.LocalDateTime;

public class ResponseChartDataDTO {
  private LocalDateTime date;
  private double value;

  public ResponseChartDataDTO(LocalDateTime date, double value) {
    this.date = date;
    this.value = value;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "ResponseChartDataDTO{" + "date=" + date + ", value=" + value + '}';
  }
}
