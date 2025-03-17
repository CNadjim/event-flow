package io.github.cnadjim.eventflow.core.domain.exception.field;

import static io.github.cnadjim.eventflow.core.domain.supplier.VersionSupplier.VERSION_FIELD;

public class VersionMissingException extends MissingFieldException {

    public VersionMissingException() {
        super(VERSION_FIELD);
    }
}
