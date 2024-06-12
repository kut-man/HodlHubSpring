package com.example.hodlhub.utils;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ErrorResponse {
    private List<FieldError> errors;
    private Date date;

    private HttpStatus status;

    public ErrorResponse(String message, Date date, HttpStatus status) {
        this.errors = parseMessage(message);
        this.date = date;
        this.status = status;
    }

    public ErrorResponse() {
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    private List<FieldError> parseMessage(String message) {
        List<FieldError> errorList = new ArrayList<>();
        String[] errorStrings = message.split(";");
        for (String errorString : errorStrings) {
            String[] fieldValuePair = errorString.split(" - ");
            if (fieldValuePair.length == 2) {
                errorList.add(new FieldError(fieldValuePair[0], fieldValuePair[1]));
            }
        }
        return errorList;
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
}
