package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.annotation.Stub;
import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import io.github.cnadjim.eventflow.core.domain.handler.*;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Stub
public class InMemoryHandlerRegistry implements HandlerRegistry {

    private final ConcurrentMap<Class<?>, EventHandler> eventHandlers = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, QueryHandler> queryHandlers = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, CommandHandler> commandHandlers = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, EventSourcingHandler> eventSourcingHandlers = new ConcurrentHashMap<>();

    @Override
    public void registerHandler(Handler handler) {
        if (isNull(handler)) throw new BadArgumentException("Handler cannot be null");

        final Class<?> payloadClass = handler.payloadClass();

        if (isNull(payloadClass)) throw new BadArgumentException("PayloadClass cannot be null");

        switch (handler) {
            case CommandHandler commandHandler -> commandHandlers.put(payloadClass, commandHandler);
            case QueryHandler queryHandler -> queryHandlers.put(payloadClass, queryHandler);
            case EventHandler eventHandler -> eventHandlers.put(payloadClass, eventHandler);
            case EventSourcingHandler eventSourcingHandler -> eventSourcingHandlers.put(payloadClass, eventSourcingHandler);
            default -> throw new IllegalStateException("Unexpected value: " + handler);
        }
    }

    @Override
    public Optional<CommandHandler> findCommandHandler(Class<?> messagePayloadClass) {
        return Optional.ofNullable(commandHandlers.get(messagePayloadClass));
    }

    @Override
    public Optional<EventHandler> findEventHandler(Class<?> messagePayloadClass) {
        return Optional.ofNullable(eventHandlers.get(messagePayloadClass));
    }

    @Override
    public Optional<EventSourcingHandler> findEventSourcingHandler(Class<?> messagePayloadClass) {
        return Optional.ofNullable(eventSourcingHandlers.get(messagePayloadClass));
    }

    @Override
    public Optional<QueryHandler> findQueryHandler(Class<?> messagePayloadClass) {
        return Optional.ofNullable(queryHandlers.get(messagePayloadClass));
    }
}
