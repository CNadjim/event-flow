package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.exception.HandlerNotFoundException;
import io.github.cnadjim.eventflow.core.domain.handler.*;

import java.util.Optional;

/**
 * Service Provider Interface for handler registry implementations.
 * The handler registry is responsible for storing and retrieving handlers for different message types.
 * It provides methods to register handlers and find handlers for specific message payload classes.
 */
public interface HandlerRegistry {

    /**
     * Registers a handler in the registry.
     *
     * @param handler The handler to register
     */
    void registerHandler(Handler handler);

    /**
     * Finds an event handler for the specified message payload class.
     *
     * @param messagePayloadClass The class of the message payload
     * @return An Optional containing the event handler if found, or empty if not found
     */
    Optional<EventHandler> findEventHandler(Class<?> messagePayloadClass);

    /**
     * Finds a query handler for the specified message payload class.
     *
     * @param messagePayloadClass The class of the message payload
     * @return An Optional containing the query handler if found, or empty if not found
     */
    Optional<QueryHandler> findQueryHandler(Class<?> messagePayloadClass);

    /**
     * Finds a command handler for the specified message payload class.
     *
     * @param messagePayloadClass The class of the message payload
     * @return An Optional containing the command handler if found, or empty if not found
     */
    Optional<CommandHandler> findCommandHandler(Class<?> messagePayloadClass);

    /**
     * Finds an event sourcing handler for the specified message payload class.
     *
     * @param messagePayloadClass The class of the message payload
     * @return An Optional containing the event sourcing handler if found, or empty if not found
     */
    Optional<EventSourcingHandler> findEventSourcingHandler(Class<?> messagePayloadClass);

    /**
     * Gets an event handler for the specified message payload class.
     * Throws an exception if no handler is found.
     *
     * @param messagePayloadClass The class of the message payload
     * @return The event handler
     * @throws HandlerNotFoundException if no handler is found
     */
    default EventHandler getEventHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException {
        return findEventHandler(messagePayloadClass).orElseThrow(() -> new HandlerNotFoundException(messagePayloadClass));
    }

    /**
     * Gets a query handler for the specified message payload class.
     * Throws an exception if no handler is found.
     *
     * @param messagePayloadClass The class of the message payload
     * @return The query handler
     * @throws HandlerNotFoundException if no handler is found
     */
    default QueryHandler getQueryHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException {
        return findQueryHandler(messagePayloadClass).orElseThrow(() -> new HandlerNotFoundException(messagePayloadClass));
    }

    /**
     * Gets a command handler for the specified message payload class.
     * Throws an exception if no handler is found.
     *
     * @param messagePayloadClass The class of the message payload
     * @return The command handler
     * @throws HandlerNotFoundException if no handler is found
     */
    default CommandHandler getCommandHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException {
        return findCommandHandler(messagePayloadClass).orElseThrow(() -> new HandlerNotFoundException(messagePayloadClass));
    }

    /**
     * Gets an event sourcing handler for the specified message payload class.
     * Throws an exception if no handler is found.
     *
     * @param messagePayloadClass The class of the message payload
     * @return The event sourcing handler
     * @throws HandlerNotFoundException if no handler is found
     */
    default EventSourcingHandler getEventSourcingHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException {
        return findEventSourcingHandler(messagePayloadClass).orElseThrow(() -> new HandlerNotFoundException(messagePayloadClass));
    }
}
