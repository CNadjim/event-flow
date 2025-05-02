package io.github.cnadjim.eventflow.core.service.gateway;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.api.SendEvent;
import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.flux.MessageGateway;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.message.Query;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@DomainService
public class EventGateway implements MessageGateway<Event>, SendEvent {

    private final MessageBus messageBus;

    /**
     * Constructs a {@code CommandGateway} with the necessary {@link MessageBus} dependency.
     *
     * @param messageBus The {@link MessageBus} used to send and receive command messages.
     */
    public EventGateway(final MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    @Override
    public void subscribe(MessageSubscriber subscriber) {
        messageBus.subscribe(subscriber);
    }

    @Override
    public void publish(Message message) {
        messageBus.publish(message);
    }

    @Override
    public void onSuccess(Event message) {
        log.debug("[ {} ] event {} executed successfully", message.id(), message.payloadClassSimpleName());
    }

    @Override
    public void onError(Event message, Error error) {
        log.debug("[ {} ] event {} executed with error {}", message.id(), message.payloadClassSimpleName(), error.message());
    }

    @Override
    public CompletableFuture<Void> send(Event event) {
        return sendAndSubscribe(event)
                .thenApplyAsync(messageResult ->  null);
    }
}
