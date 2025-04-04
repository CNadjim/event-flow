package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.annotation.UseCase;
import io.github.cnadjim.eventflow.core.domain.ResponseType;

import java.util.concurrent.CompletableFuture;

@UseCase
public interface SendQuery {

    <QUERY_RESPONSE, QUERY_REQUEST> CompletableFuture<QUERY_RESPONSE> send(QUERY_REQUEST query, ResponseType<QUERY_RESPONSE> responseType);
}
