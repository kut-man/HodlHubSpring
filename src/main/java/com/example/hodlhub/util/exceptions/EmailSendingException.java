package com.example.hodlhub.util.exceptions;

public class EmailSendingException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Failed to send email. Please try again later.";
    private final String path;

    public EmailSendingException(String message, String path) {
        super(message);
        this.path = path;
    }

    public EmailSendingException(String path) {
        super(DEFAULT_MESSAGE);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
