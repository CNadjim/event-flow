package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.api.SendCommand;
import io.github.cnadjim.eventflow.core.domain.AggregateWrapper;
import io.github.cnadjim.eventflow.core.domain.CommandWrapper;
import io.github.cnadjim.eventflow.core.domain.EventWrapper;
import io.github.cnadjim.eventflow.core.domain.handler.CommandHandler;
import io.github.cnadjim.eventflow.core.domain.handler.EventSourcingHandler;
import io.github.cnadjim.eventflow.core.spi.AggregateStore;
import io.github.cnadjim.eventflow.core.spi.EventPublisher;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@DomainService
public class CommandGateway implements SendCommand {

    private final EventStore eventStore;
    private final AggregateStore aggregateStore;
    private final EventPublisher eventPublisher;
    private final HandlerRegistry handlerRegistry;

    public CommandGateway(final EventStore eventStore,
                          final AggregateStore aggregateStore,
                          final EventPublisher eventPublisher,
                          final HandlerRegistry handlerRegistry) {
        this.eventStore = eventStore;
        this.aggregateStore = aggregateStore;
        this.eventPublisher = eventPublisher;
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public CompletableFuture<String> sendCommand(Object commandPayload) {
        return CompletableFuture.supplyAsync(() -> sendCommandSync(commandPayload));
    }

    private String sendCommandSync(Object commandPayload) {
        final CommandWrapper command = CommandWrapper.create(commandPayload);
        final String aggregateId = command.aggregateId();

        log.debug("Handling command: {} ({})", command, aggregateId);

        final CommandHandler commandHandler = handlerRegistry.getCommandHandler(command.payloadClass());
        final List<EventWrapper> events = commandHandler.handle(command);

        final AggregateWrapper aggregate = loadAggregateState(aggregateId, events);

        log.debug("Aggregate {} - Final Aggregate state: {}", aggregateId, aggregate);

        eventStore.saveAll(events);
        eventPublisher.publishAll(events);

        return aggregateId;
    }

    private AggregateWrapper loadAggregateState(String aggregateId, Collection<EventWrapper> events) {

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

        AtomicLong counter = new AtomicLong(0);

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
