package io.github.cnadjim.eventflow.spring.starter.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExceptionResponse(Instant timestamp, Integer status, String error, String message, Object details) {

    public ExceptionResponse {

    }

    public static ExceptionResponse create(HttpStatus httpStatus, String message) {
        return create(httpStatus, message, null);
    }

    public static ExceptionResponse create(HttpStatus httpStatus, String message, Object details) {
        return new ExceptionResponse(
                Instant.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                details
        );
    }

    public static ExceptionResponse create(EventFlowException eventFlowException) {
        return new ExceptionResponse(
                Instant.now(),
                INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR.getReasonPhrase(),
                eventFlowException.getMessage(),
                null
        );
    }

    public static ExceptionResponse create(BaseException baseException) {
        return new ExceptionResponse(
                Instant.now(),
                Optional.ofNullable(baseException).map(ResponseStatusException::getStatusCode).map(HttpStatusCode::value).orElse(500),
                Optional.ofNullable(baseException).map(ResponseStatusException::getStatusCode).map(HttpStatusCode::value).map(HttpStatus::resolve).map(HttpStatus::getReasonPhrase).orElseGet(INTERNAL_SERVER_ERROR::getReasonPhrase),
                Optional.ofNullable(baseException).map(BaseException::getReason).orElse(null),
                null
        );
    }
}
