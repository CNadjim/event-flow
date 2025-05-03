package io.github.cnadjim.eventflow.core.service.dispatcher;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.flux.MessageDispatcher;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.handler.CommandHandler;
import io.github.cnadjim.eventflow.core.domain.aggregate.Aggregate;
import io.github.cnadjim.eventflow.core.domain.message.Command;
import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.service.AggregateService;
import io.github.cnadjim.eventflow.core.spi.ErrorConverter;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@Slf4j
@DomainService
public class CommandDispatcher implements MessageDispatcher<Command, String> {

    private final MessageBus messageBus;
    private final ErrorConverter errorConverter;
    private final HandlerRegistry handlerRegistry;
    private final AggregateService aggregateService;

    /**
     * Constructs a {@code CommandDispatcher} with the necessary dependencies.
     *
     * @param messageBus       The {@link MessageBus} for publishing messages.
     * @param errorConverter   The {@link ErrorConverter} for converting exceptions to error messages.
     * @param handlerRegistry  The {@link HandlerRegistry} for retrieving command handlers.
     * @param aggregateService The {@link AggregateService} for loading and applying aggregate state.
     */
    public CommandDispatcher(MessageBus messageBus,
                             ErrorConverter errorConverter,
                             HandlerRegistry handlerRegistry,
                             AggregateService aggregateService) {
        this.errorConverter = errorConverter;
        this.messageBus = messageBus;
        this.handlerRegistry = handlerRegistry;
        this.aggregateService = aggregateService;
    }

    @Override
    public Command convert(Message message) {
        return convert(message, Command.class);
    }

    @Override
    public Error convert(Throwable throwable) {
        return errorConverter.convert(throwable);
    }

    @Override
    public String dispatch(Command message) {
        final String aggregateId = message.aggregateId();

        final CommandHandler commandHandler = handlerRegistry.getCommandHandler(message.payloadClass());

        final Collection<Event> events = commandHandler.handle(message);

        final Aggregate aggregate = aggregateService.loadAggregateState(aggregateId, events);

        log.debug("Aggregate {} - Final Aggregate state: {}", aggregateId, aggregate);

        events.forEach(messageBus::publish);

        return aggregateId;
    }

    @Override
    public void subscribe(MessageSubscriber subscriber) {
        messageBus.subscribe(subscriber);
    }

    @Override
    public void publish(Message message) {
        messageBus.publish(message);
    }
}
