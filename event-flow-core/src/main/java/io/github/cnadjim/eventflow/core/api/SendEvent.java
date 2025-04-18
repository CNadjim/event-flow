package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.annotation.UseCase;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for sending events in the event flow system.
 * Events represent something that has happened in the system and cannot be rejected.
 */
@UseCase
public interface SendEvent {

    /**
     * Sends an event to be processed by the appropriate handlers.
     *
     * @param event The event object to be processed
     * @return A CompletableFuture that completes when the event has been processed
     */
    CompletableFuture<Void> send(Object event);
}
