package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.exception.field.AggregateIdMissingException;
import io.github.cnadjim.eventflow.core.domain.exception.field.IdMissingException;
import io.github.cnadjim.eventflow.core.domain.exception.field.PayloadMissingException;
import io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.IdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TimestampSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TopicSupplier;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

import static java.util.Objects.isNull;


public record EventWrapper(String id,
                           String topic,
                           Object payload,
                           Instant timestamp,
                           String aggregateId) implements IdSupplier, TopicSupplier, PayloadWrapper, AggregateIdSupplier, TimestampSupplier, Comparable<EventWrapper> {

    public EventWrapper {
        if (isNull(payload)) throw new PayloadMissingException();
        if (isNull(id)) throw new IdMissingException();
        if (StringUtils.isBlank(aggregateId)) throw new AggregateIdMissingException();
    }

    public static EventWrapper create(Object payload) {
        return new EventWrapper(
                IdSupplier.create(),
                payload.getClass().getSimpleName(),
                payload,
                TimestampSupplier.create(),
                AggregateIdSupplier.findAggregateId(payload).orElse(null)
        );
    }

    @Override
    public int compareTo(EventWrapper eventWrapper) {
        return timestamp().compareTo(eventWrapper.timestamp());
    }
}
