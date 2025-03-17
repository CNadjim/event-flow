package io.github.cnadjim.eventflow.core.domain.exception.field;

import static io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier.AGGREGATE_ID_FIELD;

public class AggregateIdMissingException extends MissingFieldException {

    public AggregateIdMissingException() {
        super(AGGREGATE_ID_FIELD);
    }
}
