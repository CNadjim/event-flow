package io.github.cnadjim.eventflow.core.domain.exception;

import io.github.cnadjim.eventflow.core.domain.error.NotFoundError;

public class NotFoundException extends EventFlowException {

    public NotFoundException(NotFoundError notFoundError) {
        super(notFoundError);
    }

    public NotFoundException(String message) {
        super(NotFoundError.create(message));
    }
}
