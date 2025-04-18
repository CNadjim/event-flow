package io.github.cnadjim.eventflow.core.domain.exception;

import io.github.cnadjim.eventflow.core.domain.error.BadRequestError;

public class BadArgumentException extends EventFlowException {

    public BadArgumentException(BadRequestError badRequestError) {
        super(badRequestError);
    }

    public BadArgumentException(String message) {
        super(BadRequestError.create(message));
    }
}
