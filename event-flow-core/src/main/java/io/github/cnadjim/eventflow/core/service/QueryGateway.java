package io.github.cnadjim.eventflow.core.service;

import io.github.cnadjim.eventflow.core.api.SendQuery;
import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.domain.QueryWrapper;
import io.github.cnadjim.eventflow.core.domain.ResponseType;
import io.github.cnadjim.eventflow.core.domain.handler.QueryHandler;
import io.github.cnadjim.eventflow.core.spi.HandlerRegistry;

import java.util.concurrent.CompletableFuture;

@DomainService
public class QueryGateway implements SendQuery {

    private final HandlerRegistry handlerRegistry;

    public QueryGateway(HandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    private Object querySync(Object queryPayload) {
        final QueryWrapper query = QueryWrapper.create(queryPayload);
        final QueryHandler queryHandler = handlerRegistry.getQueryHandler(query.payloadClass());
        return queryHandler.handle(query);
    }

    @Override
    public <QUERY_RESPONSE, QUERY_REQUEST> CompletableFuture<QUERY_RESPONSE> query(QUERY_REQUEST query, ResponseType<QUERY_RESPONSE> responseType) {
        return CompletableFuture.supplyAsync(() -> querySync(query)).thenApplyAsync(responseType::convert);
    }
}
