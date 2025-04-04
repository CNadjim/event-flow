package io.github.cnadjim.eventflow.core.domain.result;

import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.domain.MessageResult;
import io.github.cnadjim.eventflow.core.domain.flux.MessageResultSubscriber;

import java.util.concurrent.CompletableFuture;

public record EventResultSubscriber(Event message,
                                    CompletableFuture<Void> future) implements MessageResultSubscriber<Event> {
    @Override
    public void handleSuccess(MessageResult messageResult) {
        future.complete(null);
    }
}
