package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.exception.field.AggregateIdMissingException;
import io.github.cnadjim.eventflow.core.domain.exception.field.PayloadMissingException;
import io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.isNull;

public record CommandWrapper(Object payload,
                             String aggregateId) implements PayloadWrapper, AggregateIdSupplier {

    public CommandWrapper {
        if (isNull(payload)) throw new PayloadMissingException();
        if (StringUtils.isBlank(aggregateId)) throw new AggregateIdMissingException();
    }

    public static CommandWrapper create(Object payload) {
        return new CommandWrapper(
                payload,
                AggregateIdSupplier.getAggregateId(payload)
        );
    }
}
