package io.github.cnadjim.eventflow.spring.starter.exception;


import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventFlowException.class)
    public ResponseEntity<ExceptionResponse> handleServiceException(EventFlowException eventFlowException) {
        final Throwable rootCause = ExceptionUtils.getRootCause(eventFlowException);
        if (Objects.nonNull(rootCause) && rootCause instanceof BaseException) {
            return handleServiceException((BaseException) rootCause);
        }
        final ExceptionResponse exceptionResponse = ExceptionResponse.create(eventFlowException);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.valueOf(exceptionResponse.status()));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponse> handleServiceException(BaseException exception) {
        final ExceptionResponse exceptionResponse = ExceptionResponse.create(exception);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.valueOf(exceptionResponse.status()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
        final List<ValidationError> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(new ValidationError(fieldName, errorMessage));
        });
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final ExceptionResponse response = ExceptionResponse.create(httpStatus, "Validation error", errors);
        return new ResponseEntity<>(response, httpStatus);
    }
}
