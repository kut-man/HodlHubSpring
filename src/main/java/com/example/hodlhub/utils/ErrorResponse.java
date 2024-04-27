package com.example.hodlhub.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ErrorResponse {
    private List<FieldError> errors;
    private Date date;

    public ErrorResponse(String message, Date date) {
        this.errors = parseMessage(message);
        this.date = date;
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
        private String value;

        public FieldError(String field, String value) {
            this.field = field;
            this.value = value;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
