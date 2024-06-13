package com.example.hodlhub.utils;

import com.example.hodlhub.utils.exceptions.CoinNotExistsException;
import com.example.hodlhub.utils.exceptions.PortfolioNotExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {CoinNotExistsException.class, PortfolioNotExistsException.class})
    public ResponseEntity<ApiResponse<Void>> handleNotFoundExceptions(Exception e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiResponse<Void> response = new ApiResponse<>(
                status,
                e.getMessage(),
                "/transaction"
        );
        return new ResponseEntity<>(response, status);
    }
}
