package io.github.cnadjim.eventflow.core.service.gateway;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.api.SendQuery;
import io.github.cnadjim.eventflow.core.domain.Query;
import io.github.cnadjim.eventflow.core.domain.ResponseType;
import io.github.cnadjim.eventflow.core.domain.result.QueryResultSubscriber;
import io.github.cnadjim.eventflow.core.spi.MessageBus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@DomainService
public class QueryGateway implements SendQuery {

    private final MessageBus messageBus;

    public QueryGateway(final MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    @Override
    public <QUERY_RESPONSE, QUERY_REQUEST> CompletableFuture<QUERY_RESPONSE> send(QUERY_REQUEST query, ResponseType<QUERY_RESPONSE> responseType) {
        final CompletableFuture<QUERY_RESPONSE> resultFuture = new CompletableFuture<>();
        final Query queryMessage = Query.create(query);
        final QueryResultSubscriber<QUERY_RESPONSE> queryResultObserver = new QueryResultSubscriber<>(queryMessage, responseType, resultFuture);

        messageBus.subscribe(queryResultObserver);
        messageBus.publish(queryMessage);

        return resultFuture.orTimeout(1, TimeUnit.MINUTES);
    }

}
