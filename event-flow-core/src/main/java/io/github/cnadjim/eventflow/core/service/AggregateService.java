package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.AggregateWrapper;
import io.github.cnadjim.eventflow.core.domain.EventWrapper;
import io.github.cnadjim.eventflow.core.domain.handler.EventSourcingHandler;
import io.github.cnadjim.eventflow.core.spi.AggregateStore;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

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


    AggregateWrapper loadAggregateState(String aggregateId, Collection<EventWrapper> events) {

        AggregateWrapper aggregate = loadPreviousAggregateState(aggregateId);

        for (EventWrapper event : events) {
            final EventSourcingHandler eventSourcingHandler = handlerRegistry.getEventSourcingHandler(event.payloadClass());
            aggregate = eventSourcingHandler.apply(event, aggregate);
        }

        if (aggregate.isSnapshotEnabled() && aggregate.version() % aggregate.threshold() == 0) {
            aggregateStore.save(aggregate);
        }

        return aggregate;
    }

    private AggregateWrapper loadPreviousAggregateState(String aggregateId) {
        final Instant startTime = Instant.now();
        AggregateWrapper aggregate = aggregateStore.findTopByAggregateIdOrderByVersionDesc(aggregateId).orElseGet(() -> AggregateWrapper.create(aggregateId));

        final Iterable<EventWrapper> events;

        if (aggregate.isSnapshotEnabled()) {
            events = eventStore.findAllByAggregateIdOrderByTimestampAscStartFrom(aggregateId, aggregate.version().intValue());
        } else {
            events = eventStore.findAllByAggregateIdOrderByTimestampAsc(aggregateId);
        }

        final AtomicLong counter = new AtomicLong(0);

        for (EventWrapper event : events) {
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
