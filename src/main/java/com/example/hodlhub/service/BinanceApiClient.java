package com.example.hodlhub.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TreeMap;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class BinanceApiClient {
  private static final String BASE_URL = "https://api.binance.com/api/v3/klines";
  private final ObjectMapper objectMapper;

  public BinanceApiClient(ObjectMapper objectMapper) {

    this.objectMapper = objectMapper;
  }

  private String buildUrl(String symbol, String interval, int limit) {
    return UriComponentsBuilder.fromUriString(BASE_URL)
        .queryParam("symbol", symbol)
        .queryParam("interval", interval)
        .queryParam("limit", limit)
        .build()
        .toString();
  }

  private String buildUrl(
      String symbol, String interval, LocalDateTime startTime, LocalDateTime endTime) {
    return UriComponentsBuilder.fromUriString(BASE_URL)
        .queryParam("symbol", symbol)
        .queryParam("interval", interval)
        .queryParam(
            "startTime", startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
        .queryParam("endTime", endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
        .build()
        .toString();
  }

  public TreeMap<LocalDateTime, Double> fetchHistoricalPrices(
      String symbol, String interval, int limit) {
    String url = buildUrl(symbol, interval, limit);
    return fetchPrices(url);
  }

  public TreeMap<LocalDateTime, Double> fetchHistoricalPrices(
      String symbol, String interval, LocalDateTime startTime, LocalDateTime endTime) {
    String url = buildUrl(symbol, interval, startTime, endTime);
    return fetchPrices(url);
  }

  private TreeMap<LocalDateTime, Double> fetchPrices(String url) {
    try {
      System.out.println("Fetching prices from: " + url);
      RestTemplate restTemplate = new RestTemplate();
      String response = restTemplate.getForObject(url, String.class);
      List<List<Object>> klineData = objectMapper.readValue(response, new TypeReference<>() {});
      TreeMap<LocalDateTime, Double> priceMap = new TreeMap<>();

      for (List<Object> kline : klineData) {
        long timestamp = Long.parseLong(kline.get(0).toString());
        LocalDateTime dateTime =
            Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();

        double closePrice = Double.parseDouble(kline.get(4).toString());
        priceMap.put(dateTime, closePrice);
      }

      return priceMap;
    } catch (Exception e) {
      e.printStackTrace();
      return new TreeMap<>();
    }
  }
}
