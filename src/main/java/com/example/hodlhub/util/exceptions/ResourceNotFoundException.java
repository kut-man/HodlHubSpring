package com.example.hodlhub.util.exceptions;

public class ResourceNotFoundException extends RuntimeException {
  private static final String DEFAULT_MESSAGE = "Resource not found!";
  private final String path;

  public ResourceNotFoundException(String message, String path) {
    super(message);
    this.path = path;
  }

  public ResourceNotFoundException(String path) {
    super(DEFAULT_MESSAGE);
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
