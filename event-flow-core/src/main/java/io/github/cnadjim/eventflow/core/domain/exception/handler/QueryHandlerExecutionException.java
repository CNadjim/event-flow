package io.github.cnadjim.eventflow.core.domain.exception.handler;

public class QueryHandlerExecutionException extends HandlerExecutionException {
    public QueryHandlerExecutionException(Throwable cause) {
        super(cause);
    }

    public QueryHandlerExecutionException(String message) {
        super(message);
    }
}
