package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.exception.field.PayloadMissingException;
import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;

import java.time.Instant;

import static java.util.Objects.isNull;

public record Query(Object payload,
                    Instant timestamp) implements PayloadSupplier {
    public Query {
        if (isNull(payload)) throw new PayloadMissingException();
    }

    public static Query create(Object payload) {
        return new Query(
                payload,
                Instant.now()
        );
    }

}
