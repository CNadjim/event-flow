package io.github.cnadjim.eventflow.core.domain.exception.field;

public class IdMissingException extends MissingFieldException {
    public IdMissingException() {
        super("id");
    }
}
