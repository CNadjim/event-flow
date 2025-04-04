package io.github.cnadjim.eventflow.core.domain.result;


import io.github.cnadjim.eventflow.core.domain.MessageResult;
import io.github.cnadjim.eventflow.core.domain.Query;
import io.github.cnadjim.eventflow.core.domain.ResponseType;
import io.github.cnadjim.eventflow.core.domain.flux.MessageResultSubscriber;

import java.util.concurrent.CompletableFuture;

public record QueryResultSubscriber<QUERY_RESPONSE>(Query message,
                                                    ResponseType<QUERY_RESPONSE> responseType,
                                                    CompletableFuture<QUERY_RESPONSE> future) implements MessageResultSubscriber<Query> {
    @Override
    public void handleSuccess(MessageResult messageResult) {
        final QUERY_RESPONSE queryResponse = responseType.convert(messageResult.payload());
        future.complete(queryResponse);
    }
}
