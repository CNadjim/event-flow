package io.github.cnadjim.eventflow.core.domain.exception.field;

import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;

public class MissingFieldException extends EventFlowException {

    private final String missingField;

    public MissingFieldException(String missingField) {
        super(String.format("The required field %s was not provided.", missingField));
        this.missingField = missingField;
    }

    public String getMissingField() {
        return missingField;
    }
}
