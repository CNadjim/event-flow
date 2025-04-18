package io.github.cnadjim.eventflow.core.service.gateway;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.api.SendCommand;
import io.github.cnadjim.eventflow.core.domain.message.Command;
import io.github.cnadjim.eventflow.core.domain.flux.MessageResultSubscriber;
import io.github.cnadjim.eventflow.core.domain.subscriber.CommandResultSubscriber;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * {@code CommandGateway} is a domain service that acts as a gateway for sending commands to the system.
 * It implements the {@link SendCommand} interface and utilizes a {@link MessageBus} to publish commands
 * and subscribe to their results.  The gateway ensures commands are sent asynchronously and provides
 * a {@link CompletableFuture} for tracking the command's completion.
 */
@Slf4j
@DomainService
public class CommandGateway implements SendCommand {

    private final MessageBus messageBus;

    /**
     * Constructs a {@code CommandGateway} with the necessary {@link MessageBus} dependency.
     *
     * @param messageBus The {@link MessageBus} used to send and receive command messages.
     */
    public CommandGateway(final MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    /**
     * Sends a command to the system asynchronously.
     * It creates a {@link Command} message, subscribes a {@link CommandResultSubscriber} to the message bus
     * to receive the command result, publishes the command message, and returns a {@link CompletableFuture}
     * that will be completed with the command result.  The future is configured with a timeout of 1 minute.
     *
     * @param commandPayload The command object to send.
     * @return A {@link CompletableFuture} that will be completed with the command result.
     */
    @Override
    public CompletableFuture<String> send(Object commandPayload) {
        final CompletableFuture<String> commandResultFuture = new CompletableFuture<>();

        final Command commandMessage = new Command(commandPayload);
        final MessageResultSubscriber<Command> commandResultSubscriber = new CommandResultSubscriber(commandMessage, commandResultFuture);

        messageBus.subscribe(commandResultSubscriber);
        messageBus.publish(commandMessage);

        return commandResultFuture.orTimeout(1, TimeUnit.MINUTES);
    }

}
