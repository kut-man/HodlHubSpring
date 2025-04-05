package com.example.hodlhub.util.exceptions;

public class RecaptchaVerificationException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "reCAPTCHA verification failed";
    private final String path;

    public RecaptchaVerificationException(String message, String path) {
        super(message);
        this.path = path;
    }

    public RecaptchaVerificationException(String path) {
        super(DEFAULT_MESSAGE);
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
