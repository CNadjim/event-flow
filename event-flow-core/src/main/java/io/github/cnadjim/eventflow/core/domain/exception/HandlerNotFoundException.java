package io.github.cnadjim.eventflow.core.domain.exception;

import io.github.cnadjim.eventflow.core.domain.error.NotFoundError;

public class HandlerNotFoundException extends EventFlowException {

    public HandlerNotFoundException(Class<?> messagePayloadClass) {
        super(NotFoundError.create(String.format("No Handler found for : %s", messagePayloadClass.getSimpleName())));
    }

}
