package io.github.cnadjim.eventflow.core.service.dispatcher;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.flux.MessageDispatcher;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.handler.CommandHandler;
import io.github.cnadjim.eventflow.core.domain.message.Aggregate;
import io.github.cnadjim.eventflow.core.domain.message.Command;
import io.github.cnadjim.eventflow.core.domain.message.CommandResult;
import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.service.AggregateService;
import io.github.cnadjim.eventflow.core.spi.ErrorConverter;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import static java.util.Objects.nonNull;

/**
 * {@code CommandDispatcher} is a domain service responsible for dispatching commands to their respective
 * {@link CommandHandler}s, applying the resulting events to the aggregate, persisting events, and publishing
 * both the resulting events and a {@link CommandResult}. It implements the {@link MessageDispatcher} interface
 * for {@link Command} messages.
 */
@Slf4j
@DomainService
public class CommandDispatcher implements MessageDispatcher<Command> {


    private final MessageBus messageBus;
    private final EventStore eventStore;
    private final ErrorConverter errorConverter;
    private final HandlerRegistry handlerRegistry;
    private final AggregateService aggregateService;

    /**
     * Constructs a {@code CommandDispatcher} with the necessary dependencies.
     *
     * @param messageBus       The {@link MessageBus} for publishing messages.
     * @param eventStore       The {@link EventStore} for persisting events.
     * @param errorConverter   The {@link ErrorConverter} for converting exceptions to error messages.
     * @param handlerRegistry  The {@link HandlerRegistry} for retrieving command handlers.
     * @param aggregateService The {@link AggregateService} for loading and applying aggregate state.
     */
    public CommandDispatcher(MessageBus messageBus,
                             EventStore eventStore,
                             ErrorConverter errorConverter,
                             HandlerRegistry handlerRegistry,
                             AggregateService aggregateService) {
        this.errorConverter = errorConverter;
        this.eventStore = eventStore;
        this.messageBus = messageBus;
        this.handlerRegistry = handlerRegistry;
        this.aggregateService = aggregateService;
    }

    /**
     * Returns the message type handled by this dispatcher, which is {@link Command}.
     *
     * @return The class of the message type handled by this dispatcher.
     */
    @Override
    public Class<Command> dispatchMessageType() {
        return Command.class;
    }

    /**
     * Dispatches a command to its corresponding handler, applies the resulting events to the aggregate,
     * persists the events, and publishes both the events and a {@link CommandResult}.
     *
     * @param message The {@link Command} message to dispatch.
     */
    @Override
    public void dispatch(Command message) {
        final String aggregateId = message.aggregateId();

        try {
            log.debug("[ {} ] Dispatching command {}", message.id(), message.payloadClassSimpleName());

            final CommandHandler commandHandler = handlerRegistry.getCommandHandler(message.payloadClass());
            final Collection<Event> events = commandHandler.handle(message);

            final Aggregate aggregate = aggregateService.loadAggregateState(aggregateId, events);

            log.debug("Aggregate {} - Final Aggregate state: {}", aggregateId, aggregate);

            if (nonNull(aggregate.payload())) {
                events.forEach(eventStore::save);
            }

            events.forEach(messageBus::publish);

            messageBus.publish(CommandResult.success(message));
            log.debug("[ {} ] Dispatching command finished successfully", message.id());
        } catch (Exception exception) {
            messageBus.publish(CommandResult.failure(message, errorConverter.convert(exception)));
            log.error("[ {} ] Dispatching command finished with error", message.id(), exception);
        }
    }

    /**
     * Subscribes a {@link MessageSubscriber} to the {@link MessageBus} to receive command messages.
     *
     * @param subscriber The subscriber to register.
     */
    @Override
    public void subscribe(final MessageSubscriber<Command> subscriber) {
        messageBus.subscribe(subscriber);
    }
}
