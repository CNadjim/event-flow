package io.github.cnadjim.eventflow.spring.starter.exception;


import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventFlowException.class)
    public ResponseEntity<ExceptionResponse> handleException(EventFlowException exception) {
        final Error error = exception;
        final ExceptionResponse exceptionResponse = ExceptionResponse.create(error);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.valueOf(exceptionResponse.status()));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponse> handleServiceException(BaseException exception) {
        final ExceptionResponse exceptionResponse = ExceptionResponse.create(exception);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.valueOf(exceptionResponse.status()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(ConstraintViolationException exception) {
        final List<ValidationError> errors = exception.getConstraintViolations().stream()
                .map(ValidationError::of)
                .collect(Collectors.toList());
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ExceptionResponse response = ExceptionResponse.create(httpStatus, "Validation error", errors);
        return new ResponseEntity<>(response, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException exception) {
        final List<ValidationError> errors = exception.getBindingResult().getAllErrors().stream()
                .map(item -> (FieldError) item)
                .map(ValidationError::of)
                .toList();
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ExceptionResponse response = ExceptionResponse.create(httpStatus, "Validation error", errors);
        return new ResponseEntity<>(response, httpStatus);
    }
}
