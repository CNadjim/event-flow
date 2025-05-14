package io.github.cnadjim.eventflow.core.usecase;

import io.github.cnadjim.eventflow.annotation.UseCase;
import io.github.cnadjim.eventflow.core.domain.message.Command;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for sending commands in the event flow system.
 * Commands are requests to change the state of the system.
 */
@UseCase
public interface SendCommand {

    /**
     * Sends a command to be processed by the appropriate handler.
     *
     * @param command The command object to be processed
     * @return A CompletableFuture containing the ID of the processed command
     */
    CompletableFuture<String> send(Command command);

    default CompletableFuture<String> send(Object payload) {
        return send(new Command(payload));
    }
}
