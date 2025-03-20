package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.exception.field.PayloadMissingException;
import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;

import static java.util.Objects.isNull;

public record QueryWrapper(Object payload) implements PayloadSupplier {
    public QueryWrapper {
        if (isNull(payload)) throw new PayloadMissingException();
    }

    public static QueryWrapper create(Object payload) {
        return new QueryWrapper(
                payload
        );
    }

}
