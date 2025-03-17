package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.exception.EventFlowIllegalArgumentException;
import io.github.cnadjim.eventflow.core.domain.exception.field.AggregateIdMissingException;
import io.github.cnadjim.eventflow.core.domain.exception.field.VersionMissingException;
import io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.VersionSupplier;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.isNull;

public record Aggregate(Long version,
                        Object payload,
                        String aggregateId) implements VersionSupplier, PayloadSupplier, AggregateIdSupplier {

    public Aggregate {
        if (isNull(version)) {
            throw new VersionMissingException();
        } else if (version < 0) {
            throw new EventFlowIllegalArgumentException("Version must be greater than 0");
        }
        if (StringUtils.isBlank(aggregateId)) throw new AggregateIdMissingException();
    }

    public static Aggregate create(String aggregateId) {
        return new Aggregate(
                0L,
                null,
                aggregateId
        );
    }
}
