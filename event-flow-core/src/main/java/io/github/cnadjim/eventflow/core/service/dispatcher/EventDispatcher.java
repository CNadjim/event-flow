package io.github.cnadjim.eventflow.core.service.dispatcher;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.domain.flux.MessageDispatcher;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.handler.EventHandler;
import io.github.cnadjim.eventflow.core.domain.message.EventResult;
import io.github.cnadjim.eventflow.core.spi.ErrorConverter;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainService
public class EventDispatcher implements MessageDispatcher<Event> {

    private final MessageBus messageBus;
    private final ErrorConverter errorConverter;
    private final HandlerRegistry handlerRegistry;

    public EventDispatcher(MessageBus messageBus, ErrorConverter errorConverter, HandlerRegistry handlerRegistry) {
        this.errorConverter = errorConverter;
        this.messageBus = messageBus;
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public Class<Event> dispatchMessageType() {
        return Event.class;
    }

    @Override
    public void dispatch(Event message) {
        try {
            log.debug("[ {} ] Dispatching event {}", message.id(), message.payloadClassSimpleName());
            final EventHandler eventHandler = handlerRegistry.getEventHandler(message.payloadClass());
            eventHandler.onEvent(message);
            messageBus.publish(EventResult.success(message));
            log.debug("[ {} ] Dispatching event finished successfully", message.id());
        } catch (Exception exception) {
            messageBus.publish(EventResult.failure(message, errorConverter.convert(exception)));
            log.error("[ {} ] Dispatching event finished with error", message.id(), exception);
        }
    }

    @Override
    public void subscribe(MessageSubscriber<Event> subscriber) {
        messageBus.subscribe(subscriber);
    }
}
