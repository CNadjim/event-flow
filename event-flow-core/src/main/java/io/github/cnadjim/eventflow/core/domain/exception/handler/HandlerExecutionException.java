package io.github.cnadjim.eventflow.core.domain.exception.handler;

import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class HandlerExecutionException extends EventFlowException {

    public HandlerExecutionException(Throwable cause) {
        super(ExceptionUtils.getRootCauseMessage(cause), ExceptionUtils.getRootCause(cause));
    }

    public HandlerExecutionException(String message) {
        super(message);
    }


    public HandlerExecutionException(String message, Throwable cause) {
        super(message, ExceptionUtils.getRootCause(cause));
    }
}
