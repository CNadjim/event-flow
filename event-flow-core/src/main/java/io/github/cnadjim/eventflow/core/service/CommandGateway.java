package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.core.api.SendCommand;
import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.Aggregate;
import io.github.cnadjim.eventflow.core.domain.Command;
import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.domain.handler.CommandHandler;
import io.github.cnadjim.eventflow.core.domain.handler.EventSourcingHandler;
import io.github.cnadjim.eventflow.core.spi.EventPublisher;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@DomainService
public class CommandGateway implements SendCommand {

    private final EventStore eventStore;
    private final EventPublisher eventPublisher;
    private final HandlerRegistry handlerRegistry;

    public CommandGateway(final EventStore eventStore,
                          final EventPublisher eventPublisher,
                          final HandlerRegistry handlerRegistry) {
        this.eventStore = eventStore;
        this.eventPublisher = eventPublisher;
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public CompletableFuture<String> sendCommand(Object commandPayload) {
        return CompletableFuture.supplyAsync(() -> sendCommandSync(commandPayload));
    }

    private String sendCommandSync(Object commandPayload) {
        final Command command = Command.create(commandPayload);
        final String aggregateId = command.aggregateId();
        final CommandHandler commandHandler = handlerRegistry.getCommandHandler(command.payloadClass());
        final List<Event> events = commandHandler.handle(command);

        final Aggregate aggregate = loadAggregateState(aggregateId, events);

        eventStore.saveAll(events);
        eventPublisher.publishAll(events);

        return aggregateId;
    }

    private Aggregate loadAggregateState(String aggregateId, Collection<Event> events) {

        Aggregate aggregate = loadPreviousAggregateState(aggregateId);

        for (Event event : events) {
            final EventSourcingHandler eventSourcingHandler = handlerRegistry.getEventSourcingHandler(event.payloadClass());
            aggregate = eventSourcingHandler.apply(event, aggregate);
        }

        return aggregate;
    }

    private Aggregate loadPreviousAggregateState(String aggregateId) {

        final List<Event> events = eventStore.findAllByAggregateIdOrderByTimestampAsc(aggregateId);

        Aggregate aggregate = Aggregate.create(aggregateId);

        for (Event event : events) {
            final EventSourcingHandler eventSourcingHandler = handlerRegistry.getEventSourcingHandler(event.payloadClass());
            aggregate = eventSourcingHandler.apply(event, aggregate);
        }

        return aggregate;
    }

}
