package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.exception.HandlerNotFoundException;
import io.github.cnadjim.eventflow.core.domain.handler.*;

import java.util.Optional;

public interface HandlerRegistry {
    void registerHandler(Handler handler);

    Optional<EventHandler> findEventHandler(Class<?> messagePayloadClass);

    Optional<QueryHandler> findQueryHandler(Class<?> messagePayloadClass);

    Optional<CommandHandler> findCommandHandler(Class<?> messagePayloadClass);

    Optional<EventSourcingHandler> findEventSourcingHandler(Class<?> messagePayloadClass);

    default EventHandler getEventHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException {
        return findEventHandler(messagePayloadClass).orElseThrow(() -> new HandlerNotFoundException(messagePayloadClass));
    }

    default QueryHandler getQueryHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException {
        return findQueryHandler(messagePayloadClass).orElseThrow(() -> new HandlerNotFoundException(messagePayloadClass));
    }

    default CommandHandler getCommandHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException {
        return findCommandHandler(messagePayloadClass).orElseThrow(() -> new HandlerNotFoundException(messagePayloadClass));
    }

    default EventSourcingHandler getEventSourcingHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException {
        return findEventSourcingHandler(messagePayloadClass).orElseThrow(() -> new HandlerNotFoundException(messagePayloadClass));
    }
}
