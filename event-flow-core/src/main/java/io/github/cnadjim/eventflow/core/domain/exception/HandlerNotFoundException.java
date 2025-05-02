package io.github.cnadjim.eventflow.core.domain.exception;

import io.github.cnadjim.eventflow.core.domain.error.ResourceNotFoundError;

public class HandlerNotFoundException extends ResourceNotFoundException {

    public HandlerNotFoundException(Class<?> messagePayloadClass) {
        super(ResourceNotFoundError.create(String.format("No Handler found for : %s", messagePayloadClass.getSimpleName())));
    }

}
