package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.VersionSupplier;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.isNull;

public record Aggregate(Long version,
                        Object payload,
                        String aggregateId) implements VersionSupplier, Message, AggregateIdSupplier, Comparable<Aggregate> {

    public Aggregate {
        if (isNull(version)) {
            throw new IllegalArgumentException("version cannot be null");
        } else if (version < 0) {
            throw new IllegalArgumentException("version must be greater than 0");
        }
        if (StringUtils.isBlank(aggregateId)) throw new IllegalArgumentException("aggregateId cannot be empty");
    }

    public static Aggregate create(String aggregateId) {
        return new Aggregate(
                VersionSupplier.create(),
                null,
                aggregateId
        );
    }

    public static Aggregate upgrade(Long version, Object payload) {
        return new Aggregate(
                version,
                payload,
                AggregateIdSupplier.findAggregateId(payload).orElse(null)
        );
    }

    public int threshold() {
        return optionalPayloadClass()
                .map(aClass -> aClass.getAnnotation(io.github.cnadjim.eventflow.annotation.Aggregate.class))
                .filter(io.github.cnadjim.eventflow.annotation.Aggregate::enableSnapshot)
                .filter(annotation -> annotation.threshold() > 0)
                .map(io.github.cnadjim.eventflow.annotation.Aggregate::threshold)
                .orElse(0);
    }

    public boolean isSnapshotEnabled() {
        return optionalPayloadClass()
                .map(aClass -> aClass.getAnnotation(io.github.cnadjim.eventflow.annotation.Aggregate.class))
                .map(io.github.cnadjim.eventflow.annotation.Aggregate::enableSnapshot)
                .orElse(false);
    }

    @Override
    public int compareTo(Aggregate aggregateWrapper) {
        return version.compareTo(aggregateWrapper.version);
    }

    @Override
    public String id() {
        return aggregateId();
    }
}
