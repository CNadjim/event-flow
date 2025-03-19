package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.annotation.Stub;
import io.github.cnadjim.eventflow.core.domain.exception.handler.HandlerNotFoundException;
import io.github.cnadjim.eventflow.core.domain.handler.*;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.nonNull;

@Stub
public class InMemoryHandlerRegistry implements HandlerRegistry {

    private final ConcurrentMap<Class<?>, EventHandler> eventHandlers = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, QueryHandler> queryHandlers = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, CommandHandler> commandHandlers = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, EventSourcingHandler> eventSourcingHandlers = new ConcurrentHashMap<>();

    @Override
    public void registerHandler(Class<?> messageClass, HandlerInvoker handler) {
        if (nonNull(messageClass) && nonNull(handler)) {
            switch (handler) {
                case CommandHandler commandHandler -> commandHandlers.put(messageClass, commandHandler);
                case QueryHandler queryHandler -> queryHandlers.put(messageClass, queryHandler);
                case EventHandler eventHandler -> eventHandlers.put(messageClass, eventHandler);
                case EventSourcingHandler eventSourcingHandler -> eventSourcingHandlers.put(messageClass, eventSourcingHandler);
                default -> throw new IllegalStateException("Unexpected value: " + handler);
            }
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

    @Override
    public EventHandler getEventHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException {
        return findEventHandler(messagePayloadClass).orElseThrow(() -> new HandlerNotFoundException(EventHandler.class.getSimpleName(), messagePayloadClass.getSimpleName()));
    }

    @Override
    public QueryHandler getQueryHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException {
        return findQueryHandler(messagePayloadClass).orElseThrow(() -> new HandlerNotFoundException(QueryHandler.class.getSimpleName(), messagePayloadClass.getSimpleName()));
    }

    @Override
    public CommandHandler getCommandHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException {
        return findCommandHandler(messagePayloadClass).orElseThrow(() -> new HandlerNotFoundException(CommandHandler.class.getSimpleName(), messagePayloadClass.getSimpleName()));
    }

    @Override
    public EventSourcingHandler getEventSourcingHandler(Class<?> messagePayloadClass) throws HandlerNotFoundException {
        return findEventSourcingHandler(messagePayloadClass).orElseThrow(() -> new HandlerNotFoundException(EventSourcingHandler.class.getSimpleName(), messagePayloadClass.getSimpleName()));
    }

}
