package com.example.hodlhub.utils.annotations;

import com.example.hodlhub.utils.validators.TransactionTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TransactionTypeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTransactionType {
    String message() default "Invalid transaction type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
