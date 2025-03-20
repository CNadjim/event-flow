package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.annotation.Aggregate;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowIllegalArgumentException;
import io.github.cnadjim.eventflow.core.domain.exception.field.AggregateIdMissingException;
import io.github.cnadjim.eventflow.core.domain.exception.field.VersionMissingException;
import io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.VersionSupplier;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.isNull;

public record AggregateWrapper(Long version,
                               Object payload,
                               String aggregateId) implements VersionSupplier, PayloadWrapper, AggregateIdSupplier, Comparable<AggregateWrapper> {

    public AggregateWrapper {
        if (isNull(version)) {
            throw new VersionMissingException();
        } else if (version < 0) {
            throw new EventFlowIllegalArgumentException("Version must be greater than 0");
        }
        if (StringUtils.isBlank(aggregateId)) throw new AggregateIdMissingException();
    }

    public static AggregateWrapper create(String aggregateId) {
        return new AggregateWrapper(
                VersionSupplier.create(),
                null,
                aggregateId
        );
    }

    public static AggregateWrapper upgrade(Long version, Object payload) {
        return new AggregateWrapper(
                version,
                payload,
                AggregateIdSupplier.findAggregateId(payload).orElse(null)
        );
    }

    public int threshold() {
        return optionalPayloadClass()
                .map(aClass -> aClass.getAnnotation(Aggregate.class))
                .filter(Aggregate::enableSnapshot)
                .filter(annotation -> annotation.threshold() > 0)
                .map(Aggregate::threshold)
                .orElse(0);
    }

    public boolean isSnapshotEnabled() {
        return optionalPayloadClass()
                .map(aClass -> aClass.getAnnotation(Aggregate.class))
                .map(Aggregate::enableSnapshot)
                .orElse(false);
    }

    @Override
    public int compareTo(AggregateWrapper aggregateWrapper) {
        return version.compareTo(aggregateWrapper.version);
    }
}
