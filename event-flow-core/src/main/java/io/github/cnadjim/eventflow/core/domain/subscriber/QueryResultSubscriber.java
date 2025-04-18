package io.github.cnadjim.eventflow.core.domain.subscriber;


import io.github.cnadjim.eventflow.core.domain.message.MessageResult;
import io.github.cnadjim.eventflow.core.domain.message.Query;
import io.github.cnadjim.eventflow.core.domain.response.ResponseType;
import io.github.cnadjim.eventflow.core.domain.flux.MessageResultSubscriber;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public record QueryResultSubscriber<QUERY_RESPONSE>(Query message,
                                                    ResponseType<QUERY_RESPONSE> responseType,
                                                    CompletableFuture<QUERY_RESPONSE> future) implements MessageResultSubscriber<Query> {
    @Override
    public void handleSuccess(MessageResult<Query> messageResult) {
        log.debug("[ {} ] Dispatching query result finished successfully", messageResult.id());
        final QUERY_RESPONSE queryResponse = responseType.convert(messageResult.payload());
        future.complete(queryResponse);
    }
}
