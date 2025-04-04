package io.github.cnadjim.eventflow.core.service.gateway;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.api.SendCommand;
import io.github.cnadjim.eventflow.core.domain.Command;
import io.github.cnadjim.eventflow.core.domain.flux.MessageResultSubscriber;
import io.github.cnadjim.eventflow.core.domain.result.CommandResultSubscriber;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@DomainService
public class CommandGateway implements SendCommand {

    private final MessageBus messageBus;

    public CommandGateway(final MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    @Override
    public CompletableFuture<String> send(Object command) {
        final CompletableFuture<String> commandResult = new CompletableFuture<>();

        final Command commandMessage = Command.create(command);
        final MessageResultSubscriber<Command> commandResultSubscriber = new CommandResultSubscriber(commandMessage, commandResult);

        messageBus.subscribe(commandResultSubscriber);
        messageBus.publish(commandMessage);

        return commandResult.orTimeout(1, TimeUnit.MINUTES);
    }

}
