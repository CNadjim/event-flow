package io.github.cnadjim.eventflow.spring.starter.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import org.springframework.validation.FieldError;

import java.util.stream.StreamSupport;

public record ValidationError(String fieldName, String errorMessage) {

    public static ValidationError of(FieldError fieldError) {
        return new ValidationError(fieldError.getField(), fieldError.getDefaultMessage());
    }

    public static ValidationError of(ConstraintViolation<?> constraintViolation) {
        final String field  = StreamSupport.stream(constraintViolation.getPropertyPath().spliterator(), false)
                .reduce((first, second) -> second)
                .map(Path.Node::getName)
                .orElse("Unknown");
        return new ValidationError(field, constraintViolation.getMessage());
    }
}
