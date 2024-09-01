package com.example.hodlhub.utils.exceptions;

public class CoinNotExistsException extends RuntimeException {
  private static final String DEFAULT_MESSAGE =
      "Coin does not exist! Please provide a valid coin ticker.";
  private final String path;

  public CoinNotExistsException(String message, String path) {
    super(message);
    this.path = path;
  }

  public CoinNotExistsException(String path) {
    super(DEFAULT_MESSAGE);
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
