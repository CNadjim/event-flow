package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.IdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TimestampSupplier;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

import static java.util.Objects.isNull;

/**
 * Represents an event message in the event flow system.
 * An event is a notification that something has happened in the system.
 * Events are typically published after a command has been processed and can be handled by multiple handlers.
 *
 * @param id          The unique identifier of the event
 * @param payload     The payload of the event, containing the data about what happened
 * @param timestamp   The time when the event occurred
 * @param aggregateId The identifier of the aggregate that this event is associated with
 */
public record Event(String id,
                    Object payload,
                    Instant timestamp,
                    String aggregateId) implements Message, AggregateIdSupplier, TimestampSupplier, Comparable<Event> {

    /**
     * Compact constructor for the Event record.
     * Validates that the payload, id, and aggregateId are not null or empty.
     *
     * @throws BadArgumentException if payload is null, id is null, or aggregateId is empty
     */
    public Event {
        if (isNull(payload)) throw new BadArgumentException("Payload cannot be null");
        if (isNull(id)) throw new BadArgumentException("Id cannot be null");
        if (StringUtils.isBlank(aggregateId)) throw new BadArgumentException("AggregateId cannot be null");
    }

    public Event(Object payload) {
        this(IdSupplier.create(), payload, TimestampSupplier.create(), AggregateIdSupplier.getAggregateId(payload));
    }

    /**
     * Compares this event with another event based on their timestamps.
     * This allows events to be sorted chronologically.
     *
     * @param eventWrapper The event to compare with
     * @return A negative integer, zero, or a positive integer as this event's timestamp
     * is less than, equal to, or greater than the specified event's timestamp
     */
    @Override
    public int compareTo(Event eventWrapper) {
        return timestamp().compareTo(eventWrapper.timestamp());
    }
}
