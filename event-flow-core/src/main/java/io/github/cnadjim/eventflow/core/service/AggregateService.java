package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.Aggregate;
import io.github.cnadjim.eventflow.core.domain.Event;
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

@Slf4j
@DomainService
public class AggregateService {

    private final EventStore eventStore;
    private final AggregateStore aggregateStore;
    private final HandlerRegistry handlerRegistry;

    public AggregateService(final EventStore eventStore,
                            final AggregateStore aggregateStore,
                            final HandlerRegistry handlerRegistry) {
        this.eventStore = eventStore;
        this.aggregateStore = aggregateStore;
        this.handlerRegistry = handlerRegistry;
    }


    public Aggregate loadAggregateState(String aggregateId, Collection<Event> events) {

        Aggregate aggregate = loadPreviousAggregateState(aggregateId);

        for (Event event : events) {
            final EventSourcingHandler eventSourcingHandler = handlerRegistry.getEventSourcingHandler(event.payloadClass());
            aggregate = eventSourcingHandler.apply(event, aggregate);
        }

        if (isNull(aggregate.payload())) {
            eventStore.deleteAllByAggregateId(aggregateId);
            aggregateStore.deleteAllByAggregateId(aggregateId);
        } else if (aggregate.isSnapshotEnabled() && aggregate.version() % aggregate.threshold() == 0) {
            aggregateStore.save(aggregate);
        }

        return aggregate;
    }

    private Aggregate loadPreviousAggregateState(String aggregateId) {
        final Instant startTime = Instant.now();
        Aggregate aggregate = aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId).orElseGet(() -> Aggregate.create(aggregateId));

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
