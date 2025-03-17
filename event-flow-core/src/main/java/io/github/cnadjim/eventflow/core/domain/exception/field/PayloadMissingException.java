package io.github.cnadjim.eventflow.core.domain.exception.field;

import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;

public class PayloadMissingException extends MissingFieldException {

    public PayloadMissingException() {
        super(PayloadSupplier.PAYLOAD_FIELD);
    }
}
