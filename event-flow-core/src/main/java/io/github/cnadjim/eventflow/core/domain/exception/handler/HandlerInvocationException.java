package io.github.cnadjim.eventflow.core.domain.exception.handler;

import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class HandlerInvocationException extends EventFlowException {
    public HandlerInvocationException(Throwable cause) {
        super(ExceptionUtils.getRootCauseMessage(cause), ExceptionUtils.getRootCause(cause));
    }
}
