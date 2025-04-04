package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.IdSupplier;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.isNull;

public record Command(String id,
                      Object payload,
                      String aggregateId) implements Message, AggregateIdSupplier {

    public Command {
        if (isNull(payload)) throw new IllegalArgumentException("payload cannot be null");
        if (StringUtils.isBlank(aggregateId)) throw new IllegalArgumentException("aggregateId cannot be empty");
    }

    public static Command create(Object payload) {
        return new Command(
                IdSupplier.create(),
                payload,
                AggregateIdSupplier.getAggregateId(payload)
        );
    }
}
