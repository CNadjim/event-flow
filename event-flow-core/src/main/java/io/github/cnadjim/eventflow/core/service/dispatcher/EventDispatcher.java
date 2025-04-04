package io.github.cnadjim.eventflow.core.service.dispatcher;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.domain.MessageResult;
import io.github.cnadjim.eventflow.core.domain.flux.MessageDispatcher;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.handler.EventHandler;
import io.github.cnadjim.eventflow.core.spi.ErrorConverter;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainService
public class EventDispatcher implements MessageDispatcher<Event> {

    private final ErrorConverter errorConverter;
    private final MessageBus messageBus;
    private final HandlerRegistry handlerRegistry;

    public EventDispatcher(ErrorConverter errorConverter, MessageBus messageBus, HandlerRegistry handlerRegistry) {
        this.errorConverter = errorConverter;
        this.messageBus = messageBus;
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public Class<Event> classOfMessage() {
        return Event.class;
    }

    @Override
    public void dispatch(Event message) {
        try {
            final EventHandler eventHandler = handlerRegistry.getEventHandler(message.payloadClass());
            eventHandler.onEvent(message);
            messageBus.publish(MessageResult.success(message));
        } catch (Exception exception) {
            messageBus.publish(MessageResult.failure(message, exception, errorConverter));
        }
    }

    @Override
    public void subscribe(MessageSubscriber<Event> subscriber) {
        messageBus.subscribe(subscriber);
    }
}
