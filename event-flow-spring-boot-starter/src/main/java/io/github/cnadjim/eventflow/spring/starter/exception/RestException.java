package io.github.cnadjim.eventflow.spring.starter.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class RestException extends BaseException {

    public RestException(HttpStatus status, String message) {
        super(status, message);
    }
}
