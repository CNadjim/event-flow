package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.IdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TimestampSupplier;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

import static java.util.Objects.isNull;


public record Event(String id,
                    Object payload,
                    Instant timestamp,
                    String aggregateId) implements Message, AggregateIdSupplier, TimestampSupplier, Comparable<Event> {

    public Event {
        if (isNull(payload)) throw new IllegalArgumentException("Payload cannot be null");
        if (isNull(id)) throw new IllegalArgumentException("Id cannot be null");
        if (StringUtils.isBlank(aggregateId)) throw new IllegalArgumentException("AggregateId cannot be null");
    }

    public static Event create(Object payload) {
        return new Event(
                IdSupplier.create(),
                payload,
                TimestampSupplier.create(),
                AggregateIdSupplier.findAggregateId(payload).orElse(null)
        );
    }

    @Override
    public int compareTo(Event eventWrapper) {
        return timestamp().compareTo(eventWrapper.timestamp());
    }
}
