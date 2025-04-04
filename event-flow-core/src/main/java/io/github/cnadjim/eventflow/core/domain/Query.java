package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.supplier.IdSupplier;

import static java.util.Objects.isNull;

public record Query(String id,
                    Object payload) implements Message {
    public Query {
        if (isNull(payload)) throw new IllegalArgumentException("payload cannot be null");
    }

    public static Query create(Object payload) {
        return new Query(
                IdSupplier.create(),
                payload
        );
    }

}
