package io.github.cnadjim.eventflow.core.domain.subscriber;

import io.github.cnadjim.eventflow.core.domain.message.Command;
import io.github.cnadjim.eventflow.core.domain.message.MessageResult;
import io.github.cnadjim.eventflow.core.domain.flux.MessageResultSubscriber;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public record CommandResultSubscriber(Command message,
                                      CompletableFuture<String> future) implements MessageResultSubscriber<Command> {

    @Override
    public void handleSuccess(MessageResult<Command> messageResult) {
        log.debug("Command result finished successfully {}", messageResult.id());
        future.complete(message.aggregateId());
    }
}
