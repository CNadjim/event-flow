package io.github.cnadjim.eventflow.spring.starter.exception;

public record ValidationError(String fieldName, String errorMessage) {
}
