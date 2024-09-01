package com.example.hodlhub.utils;

import com.example.hodlhub.utils.exceptions.CoinNotExistsException;
import com.example.hodlhub.utils.exceptions.PortfolioNotExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(value = {PortfolioNotExistsException.class})
  public ResponseEntity<ApiResponse<Void>> handlePortfolioNotFoundException(
      PortfolioNotExistsException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    ApiResponse<Void> response = new ApiResponse<>(status, e.getMessage(), e.getPath());
    return new ResponseEntity<>(response, status);
  }

  @ExceptionHandler(value = {CoinNotExistsException.class})
  public ResponseEntity<ApiResponse<Void>> handleCoinNotFoundException(CoinNotExistsException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    ApiResponse<Void> response = new ApiResponse<>(status, e.getMessage(), e.getPath());
    return new ResponseEntity<>(response, status);
  }

  @ExceptionHandler(value = {DateTimeParseException.class})
  public ResponseEntity<ApiResponse<Void>> handleDateTimeParseException(DateTimeParseException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    ApiResponse<Void> response =
        new ApiResponse<>(
            status,
            "Invalid date/time format. Please ensure the date/time is in the correct format: 'EEE MMM dd yyyy HH:mm:ss 'GMT'Z (Timezone)'.",
            "/transaction");
    return new ResponseEntity<>(response, status);
  }
}
