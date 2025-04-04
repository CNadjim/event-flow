package io.github.cnadjim.eventflow.core.domain.result;

import io.github.cnadjim.eventflow.core.domain.Command;
import io.github.cnadjim.eventflow.core.domain.MessageResult;
import io.github.cnadjim.eventflow.core.domain.flux.MessageResultSubscriber;

import java.util.concurrent.CompletableFuture;

public record CommandResultSubscriber(Command message,
                                      CompletableFuture<String> future) implements MessageResultSubscriber<Command> {

    @Override
    public void handleSuccess(MessageResult messageResult) {
        future.complete(message.aggregateId());
    }
}
