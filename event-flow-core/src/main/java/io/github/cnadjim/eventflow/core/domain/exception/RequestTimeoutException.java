package io.github.cnadjim.eventflow.core.domain.exception;

import io.github.cnadjim.eventflow.core.domain.error.RequestTimeoutError;

public class RequestTimeoutException extends EventFlowException {

    public RequestTimeoutException(String message) {
        super(RequestTimeoutError.create(message));
    }
}
