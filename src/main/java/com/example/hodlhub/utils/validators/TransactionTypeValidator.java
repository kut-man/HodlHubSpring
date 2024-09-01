package com.example.hodlhub.utils.validators;

import com.example.hodlhub.utils.enums.TransactionType;
import com.example.hodlhub.utils.annotations.ValidTransactionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TransactionTypeValidator implements ConstraintValidator<ValidTransactionType, String> {

  @Override
  public void initialize(ValidTransactionType constraintAnnotation) {}

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }
    try {
      TransactionType.valueOf(value.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
