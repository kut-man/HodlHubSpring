package com.example.hodlhub.utils;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ApiResponse<T> {
  private int status;
  private T data;
  private String code;
  private String message;
  private List<FieldError> errors;
  private String path;
  private LocalDateTime timestamp;

  public ApiResponse(HttpStatus status, T data, String path) {
    this.status = status.value();
    this.data = data;
    this.code = status.getReasonPhrase();
    this.path = path;
    this.timestamp = LocalDateTime.now();
  }

  public ApiResponse(HttpStatus status, String message, String path) {
    this.status = status.value();
    this.code = status.getReasonPhrase();
    this.message = message;
    this.path = path;
    this.timestamp = LocalDateTime.now();
  }

  public ApiResponse(HttpStatus status, BindingResult bindingResult, String path) {
    this.status = status.value();
    this.code = status.getReasonPhrase();
    this.path = path;
    this.timestamp = LocalDateTime.now();

    if (bindingResult.hasErrors()) {
      this.message = "Validation failed. Please check your inputs.";
      this.errors =
          bindingResult.getFieldErrors().stream()
              .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
              .collect(Collectors.toList());
    }
  }

  public static class FieldError {
    private String field;
    private String message;

    public FieldError(String field, String message) {
      this.field = field;
      this.message = message;
    }

    public String getField() {
      return field;
    }

    public void setField(String field) {
      this.field = field;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<FieldError> geterrors() {
    return errors;
  }

  public void seterrors(List<FieldError> errors) {
    this.errors = errors;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }
}
