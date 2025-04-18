package io.github.cnadjim.eventflow.core.service.gateway;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.api.SendEvent;
import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.domain.subscriber.EventResultSubscriber;
import io.github.cnadjim.eventflow.core.spi.MessageBus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * {@code EventGateway} is a domain service responsible for sending events to the system through a {@link MessageBus}.
 * It implements the {@link SendEvent} interface, providing a method to asynchronously send events and receive a
 * completion signal via a {@link CompletableFuture}.
 */
@DomainService
public class EventGateway implements SendEvent {

    private final MessageBus messageBus;

    /**
     * Constructs an {@code EventGateway} with the required {@link MessageBus} dependency.
     *
     * @param messageBus The {@link MessageBus} used to publish events.
     */
    public EventGateway(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    /**
     * Sends an event to the system asynchronously.
     * It creates an {@link Event} message, subscribes an {@link EventResultSubscriber} to listen for the event's completion,
     * publishes the event message to the {@link MessageBus}, and returns a {@link CompletableFuture} that completes when
     * the event has been processed.  The future is configured with a timeout of 1 minute.
     *
     * @param eventPayload The event object to send.
     * @return A {@link CompletableFuture} that completes when the event has been processed.
     */
    @Override
    public CompletableFuture<Void> send(Object eventPayload) {
        final CompletableFuture<Void> eventResult = new CompletableFuture<>();
        final Event eventMessage = new Event(eventPayload);
        final EventResultSubscriber eventResultObserver = new EventResultSubscriber(eventMessage, eventResult);

        messageBus.subscribe(eventResultObserver);
        messageBus.publish(eventMessage);

        return eventResult.orTimeout(1, TimeUnit.MINUTES);
    }
}
