package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.annotation.UseCase;
import io.github.cnadjim.eventflow.core.domain.message.Command;
import io.github.cnadjim.eventflow.core.domain.message.Event;

import java.util.concurrent.CompletableFuture;


@UseCase
public interface SendEvent {

    CompletableFuture<Void> send(Event event);

    default CompletableFuture<Void> send(Object payload) {
        return send(new Event(payload));
    }
}
