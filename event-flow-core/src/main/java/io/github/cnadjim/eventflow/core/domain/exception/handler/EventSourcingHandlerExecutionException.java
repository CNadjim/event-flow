package io.github.cnadjim.eventflow.core.domain.exception.handler;

public class EventSourcingHandlerExecutionException extends HandlerExecutionException {
    public EventSourcingHandlerExecutionException(Throwable cause) {
        super(cause);
    }
}
