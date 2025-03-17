package io.github.cnadjim.eventflow.core.domain.exception;

public abstract class EventFlowException extends RuntimeException {

    public EventFlowException() {
        super();
    }

    public EventFlowException(String message) {
        super(message);
    }

    public EventFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventFlowException(Throwable cause) {
        super(cause);
    }

}
