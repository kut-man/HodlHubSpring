package com.example.hodlhub.util;

import com.example.hodlhub.util.exceptions.CoinNotExistsException;
import com.example.hodlhub.util.exceptions.PortfolioNotExistsException;
import java.time.format.DateTimeParseException;

import com.example.hodlhub.util.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

  @ExceptionHandler(value = {ResourceNotFoundException.class})
  public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException e) {
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
