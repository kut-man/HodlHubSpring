package com.example.hodlhub.utils.exceptions;

public class CoinNotExistsException extends RuntimeException {
    public CoinNotExistsException(String message) {
        super(message);
    }
}
