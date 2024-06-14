package com.example.hodlhub.utils.exceptions;

public class PortfolioNotExistsException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Portfolio does not exist! Please provide a valid portfolio ID.";
    private final String path;

    public PortfolioNotExistsException(String message, String path) {
        super(message);
        this.path = path;
    }

    public PortfolioNotExistsException(String path) {
        super(DEFAULT_MESSAGE);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
