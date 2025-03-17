package io.github.cnadjim.eventflow.core.domain.exception.handler;

public class EventHandlerExecutionException extends HandlerExecutionException {
    public EventHandlerExecutionException(Throwable cause) {
        super(cause);
    }
}
