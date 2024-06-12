package com.example.hodlhub.utils.exceptions;

public class PortfolioNotExistsException extends RuntimeException {
    public PortfolioNotExistsException(String message) {
        super(message);
    }
}
