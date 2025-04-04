package io.github.cnadjim.eventflow.core.service.gateway;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.api.SendEvent;
import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.domain.result.EventResultSubscriber;
import io.github.cnadjim.eventflow.core.spi.MessageBus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@DomainService
public class EventGateway implements SendEvent {

    private final MessageBus messageBus;

    public EventGateway(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    @Override
    public CompletableFuture<Void> send(Object event) {
        final CompletableFuture<Void> eventResult = new CompletableFuture<>();
        final Event eventMessage = Event.create(event);
        final EventResultSubscriber eventResultObserver = new EventResultSubscriber(eventMessage, eventResult);

        messageBus.subscribe(eventResultObserver);
        messageBus.publish(eventMessage);

        return eventResult.orTimeout(1, TimeUnit.MINUTES);
    }
}
