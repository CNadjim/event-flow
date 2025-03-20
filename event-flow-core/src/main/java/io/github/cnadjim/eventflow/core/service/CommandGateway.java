package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.api.SendCommand;
import io.github.cnadjim.eventflow.core.domain.AggregateWrapper;
import io.github.cnadjim.eventflow.core.domain.CommandWrapper;
import io.github.cnadjim.eventflow.core.domain.EventWrapper;
import io.github.cnadjim.eventflow.core.domain.handler.CommandHandler;
import io.github.cnadjim.eventflow.core.spi.EventPublisher;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@DomainService
public class CommandGateway implements SendCommand {

    private final EventStore eventStore;
    private final EventPublisher eventPublisher;
    private final HandlerRegistry handlerRegistry;

    private final AggregateService aggregateService;

    public CommandGateway(final EventStore eventStore,
                          final EventPublisher eventPublisher,
                          final HandlerRegistry handlerRegistry,
                          final AggregateService aggregateService) {
        this.eventStore = eventStore;
        this.aggregateService = aggregateService;
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

        final AggregateWrapper aggregate = aggregateService.loadAggregateState(aggregateId, events);

        log.debug("Aggregate {} - Final Aggregate state: {}", aggregateId, aggregate);

        eventStore.saveAll(events);
        eventPublisher.publishAll(events);

        return aggregateId;
    }


}
