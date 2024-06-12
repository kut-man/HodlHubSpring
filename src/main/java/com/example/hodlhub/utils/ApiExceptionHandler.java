package com.example.hodlhub.utils;

import com.example.hodlhub.utils.exceptions.CoinNotExistsException;
import com.example.hodlhub.utils.exceptions.PortfolioNotExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {CoinNotExistsException.class})
    public ResponseEntity<Object> handleCoinNotExistsException(CoinNotExistsException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), new Date(), status);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(value = {PortfolioNotExistsException.class})
    public ResponseEntity<Object> handlePortfolioNotExistsException(PortfolioNotExistsException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), new Date(), status);
        return new ResponseEntity<>(errorResponse, status);
    }
}
