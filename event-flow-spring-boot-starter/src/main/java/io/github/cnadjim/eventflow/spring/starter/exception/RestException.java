package io.github.cnadjim.eventflow.spring.starter.exception;

import org.springframework.http.HttpStatus;

public class RestException extends BaseException {

    public RestException(HttpStatus status, String message) {
        super(status, message);
    }
}
