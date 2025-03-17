package io.github.cnadjim.eventflow.core.domain.exception.handler;

public class CommandHandlerExecutionException extends HandlerExecutionException {
    public CommandHandlerExecutionException(Throwable cause) {
        super(cause);
    }

    public CommandHandlerExecutionException(String message) {
        super(message);
    }
}
