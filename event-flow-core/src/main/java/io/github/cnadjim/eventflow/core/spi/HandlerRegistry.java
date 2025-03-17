package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.exception.handler.HandlerNotFoundException;
import io.github.cnadjim.eventflow.core.domain.handler.*;

import java.util.Optional;

public interface HandlerRegistry {
    void registerHandler(Class<?> messagePayloadClass, HandlerInvoker handler);

    Optional<EventHandler> findEventHandler(Class<?> messagePayloadClass);

    Optional<QueryHandler> findQueryHandler(Class<?> messagePayloadClass);

    Optional<CommandHandler> findCommandHandler(Class<?> messagePayloadClass);

    Optional<EventSourcingHandler> findEventSourcingHandler(Class<?> messagePayloadClass);

    EventHandler getEventHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException;

    QueryHandler getQueryHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException;

    CommandHandler getCommandHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException;

    EventSourcingHandler getEventSourcingHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException;
}
