package io.github.cnadjim.eventflow.core.service.gateway;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.api.SendCommand;
import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.flux.MessageGateway;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.message.Command;
import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.message.MessageResult;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * {@code CommandGateway} is a domain service that acts as a gateway for sending commands to the system.
 * It implements the {@link SendCommand} interface and uses a {@link MessageBus} to publish commands
 * and subscribe to their results.  The gateway ensures commands are sent asynchronously and provides
 * a {@link CompletableFuture} for tracking the command's completion.
 */
@Slf4j
@DomainService
public class CommandGateway implements MessageGateway<Command>, SendCommand {

    private final MessageBus messageBus;

    /**
     * Constructs a {@code CommandGateway} with the necessary {@link MessageBus} dependency.
     *
     * @param messageBus The {@link MessageBus} used to send and receive command messages.
     */
    public CommandGateway(final MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    @Override
    public void onSuccess(Command message) {
        log.debug("[ {} ] command {} executed successfully", message.id(), message.payloadClassSimpleName());
    }

    @Override
    public void onError(Command message, Error error) {
        log.debug("[ {} ] command {} executed with error {}", message.id(), message.payloadClassSimpleName(), error.message());
    }

    @Override
    public CompletableFuture<String> send(Command command) {
        return sendAndSubscribe(command)
                .thenApplyAsync(MessageResult::payload)
                .thenApplyAsync(Object::toString);
    }

    @Override
    public void subscribe(MessageSubscriber subscriber) {
        messageBus.subscribe(subscriber);
    }

    @Override
    public void publish(Message message) {
        messageBus.publish(message);
    }
}
