package io.github.cnadjim.eventflow.core.domain.exception;

import io.github.cnadjim.eventflow.core.domain.error.ResourceNotFoundError;

public class ResourceNotFoundException extends EventFlowException {

    public ResourceNotFoundException(ResourceNotFoundError notFoundError) {
        super(notFoundError);
    }

    public ResourceNotFoundException(String message) {
        super(ResourceNotFoundError.create(message));
    }
}
