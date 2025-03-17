package io.github.cnadjim.eventflow.spring.starter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class BaseException extends ResponseStatusException {

    public BaseException(HttpStatus status) {
        super(status);
    }

    public BaseException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public BaseException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }

    public BaseException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    protected BaseException(HttpStatus status, String reason, Throwable cause, String messageDetailCode, Object[] messageDetailArguments) {
        super(status, reason, cause, messageDetailCode, messageDetailArguments);
    }
}
