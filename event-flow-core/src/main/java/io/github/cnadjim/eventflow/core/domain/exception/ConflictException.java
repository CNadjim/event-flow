package io.github.cnadjim.eventflow.core.domain.exception;

import io.github.cnadjim.eventflow.core.domain.error.ConflictError;

public class ConflictException extends EventFlowException {

    public ConflictException(String message) {
        super(ConflictError.create(message));
    }
}
