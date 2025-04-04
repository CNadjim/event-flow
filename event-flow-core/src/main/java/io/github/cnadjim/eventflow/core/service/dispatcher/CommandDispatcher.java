package io.github.cnadjim.eventflow.core.service.dispatcher;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.Aggregate;
import io.github.cnadjim.eventflow.core.domain.Command;
import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.domain.MessageResult;
import io.github.cnadjim.eventflow.core.domain.flux.MessageDispatcher;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.handler.CommandHandler;
import io.github.cnadjim.eventflow.core.service.AggregateService;
import io.github.cnadjim.eventflow.core.spi.ErrorConverter;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import static java.util.Objects.nonNull;

@Slf4j
@DomainService
public class CommandDispatcher implements MessageDispatcher<Command> {

    private final ErrorConverter errorConverter;
    private final MessageBus messageBus;
    private final EventStore eventStore;
    private final HandlerRegistry handlerRegistry;
    private final AggregateService aggregateService;

    public CommandDispatcher(ErrorConverter errorConverter,
                             MessageBus messageBus,
                             EventStore eventStore,
                             HandlerRegistry handlerRegistry,
                             AggregateService aggregateService) {
        this.errorConverter = errorConverter;
        this.eventStore = eventStore;
        this.messageBus = messageBus;
        this.handlerRegistry = handlerRegistry;
        this.aggregateService = aggregateService;
    }

    @Override
    public Class<Command> classOfMessage() {
        return Command.class;
    }

    @Override
    public void dispatch(Command message) {
        try {
            final String aggregateId = message.aggregateId();

            log.debug("Handling command: {} ({})", message, aggregateId);

            final CommandHandler commandHandler = handlerRegistry.getCommandHandler(message.payloadClass());
            final Collection<Event> events = commandHandler.handle(message);

            final Aggregate aggregate = aggregateService.loadAggregateState(aggregateId, events);

            log.debug("Aggregate {} - Final Aggregate state: {}", aggregateId, aggregate);

            if (nonNull(aggregate.payload())) {
                events.forEach(eventStore::save);
            }

            events.forEach(messageBus::publish);

            messageBus.publish(MessageResult.success(message, message.aggregateId()));
        } catch (Exception exception) {
            messageBus.publish(MessageResult.failure(message, exception, errorConverter));
        }
    }

    @Override
    public void subscribe(final MessageSubscriber<Command> subscriber) {
        messageBus.subscribe(subscriber);
    }
}
