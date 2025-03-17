package io.github.cnadjim.eventflow.core.domain.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ScanPackageExecutionException extends EventFlowException {
    public ScanPackageExecutionException(Throwable cause) {
        super(ExceptionUtils.getRootCauseMessage(cause), ExceptionUtils.getRootCause(cause));
    }
}
