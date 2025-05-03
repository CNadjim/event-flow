package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.aggregate.Aggregate;
import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.domain.handler.EventSourcingHandler;
import io.github.cnadjim.eventflow.core.spi.AggregateStore;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import lombok.extern.slf4j.Slf4j;

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


    /**
     * Loads the aggregate state by applying a collection of events to the aggregate.
     * It retrieves the previous aggregate state from the {@link AggregateStore},
     * applies the provided events using appropriate {@link EventSourcingHandler}s,
     * and persists the aggregate if snapshotting is enabled and the version threshold is met.
     *
     * @param aggregateId The ID of the aggregate.
     * @param events      The collection of events to apply.
     * @return The reconstructed {@link Aggregate} with the applied events.
     */
    public Aggregate loadAggregateState(String aggregateId, Collection<Event> events) {

        Aggregate aggregate = loadPreviousAggregateState(aggregateId);

        for (Event event : events) {
            final EventSourcingHandler eventSourcingHandler = handlerRegistry.getEventSourcingHandler(event.payloadClass());
            aggregate = eventSourcingHandler.apply(event, aggregate);
        }

        if (isNull(aggregate.payload())) {
            eventStore.deleteAllByAggregateId(aggregateId);
            aggregateStore.deleteAllByAggregateId(aggregateId);
        } else {
            events.forEach(eventStore::save);
        }

        if (aggregate.isSnapshotEnabled() && aggregate.version() % aggregate.threshold() == 0) {
            aggregateStore.save(aggregate);
        }

        return aggregate;
    }

    /**
     * Loads the previous aggregate state from the {@link AggregateStore} and applies all events
     * from the {@link EventStore} to reconstruct the current state.  If snapshotting is enabled, it loads
     * events starting from the version after the last snapshot.
     *
     * @param aggregateId The ID of the aggregate.
     * @return The reconstructed {@link Aggregate}.
     */
    private Aggregate loadPreviousAggregateState(String aggregateId) {
        final Instant startTime = Instant.now();
        Aggregate aggregate = aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId).orElseGet(() -> new Aggregate(aggregateId));

        final Iterable<Event> events;

        if (aggregate.isSnapshotEnabled()) {
            events = eventStore.findAllByAggregateIdOrderByTimestampAscStartFrom(aggregateId, aggregate.version().intValue());
        } else {
            events = eventStore.findAllByAggregateIdOrderByTimestampAsc(aggregateId);
        }

        final AtomicLong counter = new AtomicLong(0);

        for (Event event : events) {
            final EventSourcingHandler eventSourcingHandler = handlerRegistry.getEventSourcingHandler(event.payloadClass());
            aggregate = eventSourcingHandler.apply(event, aggregate);
            counter.incrementAndGet();
        }


        final Instant endTime = Instant.now();
        final Duration duration = Duration.between(startTime, endTime);

        log.debug("Aggregate {} - Number of events applied: {}", aggregateId, counter.get());
        log.debug("Aggregate {} - Aggregate state reconstructed: {}", aggregateId, aggregate);
        log.debug("Aggregate {} - Aggregate state reconstructed in: {} ms ({} sec)", aggregateId, duration.toMillis(), duration.toSeconds());

        return aggregate;
    }
}
