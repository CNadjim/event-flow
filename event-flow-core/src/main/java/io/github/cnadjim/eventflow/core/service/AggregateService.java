package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.aggregate.Aggregate;
import io.github.cnadjim.eventflow.core.domain.aggregate.AggregateLifecycle;
import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.domain.handler.EventSourcingHandler;
import io.github.cnadjim.eventflow.core.port.AggregateStore;
import io.github.cnadjim.eventflow.core.port.EventStore;
import io.github.cnadjim.eventflow.core.port.HandlerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.isNull;

/**
 * {@code AggregateService} is a domain service responsible for managing the state of aggregates
 * by applying events sourced from the {@link EventStore}. It reconstructs the aggregate state
 * from events and snapshots, and it persists aggregates based on snapshot settings.
 */
@Slf4j
@DomainService
public class AggregateService {

    private final EventStore eventStore;
    private final AggregateStore aggregateStore;
    private final HandlerRegistry handlerRegistry;

    /**
     * Constructs an {@code AggregateService} with the necessary dependencies.
     *
     * @param eventStore      The {@link EventStore} for retrieving events.
     * @param aggregateStore  The {@link AggregateStore} for persisting aggregate snapshots.
     * @param handlerRegistry The {@link HandlerRegistry} for retrieving event-sourcing handlers.
     */
    public AggregateService(final EventStore eventStore,
                            final AggregateStore aggregateStore,
                            final HandlerRegistry handlerRegistry) {
        this.eventStore = eventStore;
        this.aggregateStore = aggregateStore;
        this.handlerRegistry = handlerRegistry;
    }


    public void applyNewEvent(Aggregate aggregate, Event event) {
        final String aggregateId = aggregate.aggregateId();

        AggregateLifecycle.resetDeletedMarker();

        final EventSourcingHandler eventSourcingHandler = handlerRegistry.getEventSourcingHandler(event.payloadClass());

        aggregate = eventSourcingHandler.apply(event, aggregate);

        if (AggregateLifecycle.isMarkedDeleted()) {
            eventStore.deleteAllByAggregateId(aggregateId);
            aggregateStore.deleteAllByAggregateId(aggregateId);
        } else {
            eventStore.save(event);
            if (aggregate.isSnapshotEnabled() && aggregate.version() % aggregate.threshold() == 0) {
                aggregateStore.save(aggregate);
            }
        }
    }

    public Aggregate loadAggregate(Aggregate initialAggregateState) {
        final Instant startTime = Instant.now();

        final String aggregateId = initialAggregateState.aggregateId();

        Aggregate aggregate = aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId).orElse(initialAggregateState);

        final Collection<Event> events;

        if (aggregate.isSnapshotEnabled()) {
            events = eventStore.findAllByAggregateIdOrderByTimestampAscStartFrom(aggregateId, aggregate.version().intValue());
        } else {
            events = eventStore.findAllByAggregateIdOrderByTimestampAsc(aggregateId);
        }

        if (CollectionUtils.isEmpty(events)) {
            return aggregate;
        }

        final AtomicLong counter = new AtomicLong(0);

        for (Event event : events) {
            final EventSourcingHandler eventSourcingHandler = handlerRegistry.getEventSourcingHandler(event.payloadClass());
            aggregate = eventSourcingHandler.apply(event, aggregate);
            counter.incrementAndGet();
        }

        final Instant endTime = Instant.now();
        final Duration duration = Duration.between(startTime, endTime);

        log.debug("[ {} ] - Number of events applied: {}", aggregateId, counter.get());
        log.debug("[ {} ] - Aggregate state reconstructed with success in: {} ms ( {} sec )", aggregateId, duration.toMillis(), duration.toSeconds());

        return aggregate;
    }
}
