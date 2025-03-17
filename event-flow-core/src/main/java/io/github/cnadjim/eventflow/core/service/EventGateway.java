package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.core.api.SendEvent;
import io.github.cnadjim.eventflow.core.ddd.DomainService;
import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.domain.handler.EventHandler;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;

import java.util.concurrent.CompletableFuture;

import static java.util.Objects.nonNull;

@DomainService
public class EventGateway implements SendEvent {

    private final HandlerRegistry handlerRegistry;

    public EventGateway(HandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    public void sendSync(Event event) {
        final EventHandler eventHandler = handlerRegistry.findEventHandler(event.payloadClass()).orElse(null);
        if (nonNull(eventHandler)) {
            eventHandler.onEvent(event);
        }
    }

    @Override
    public void send(Event event) {
        CompletableFuture.runAsync(() -> sendSync(event));
    }
}
