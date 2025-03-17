package io.github.cnadjim.eventflow.core.domain.exception.handler;

import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;

public class HandlerNotFoundException extends EventFlowException {

    public HandlerNotFoundException(String handlerName, String messageName) {
        super(String.format("%s not found for message %s", handlerName, messageName));
    }
}
