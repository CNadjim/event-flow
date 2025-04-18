package io.github.cnadjim.eventflow.core.domain.subscriber;

import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.domain.message.MessageResult;
import io.github.cnadjim.eventflow.core.domain.flux.MessageResultSubscriber;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public record EventResultSubscriber(Event message,
                                    CompletableFuture<Void> future) implements MessageResultSubscriber<Event> {
    @Override
    public void handleSuccess(MessageResult<Event> messageResult) {
        log.debug("Event result finished successfully {}", messageResult.id());
        future.complete(null);
    }
}
