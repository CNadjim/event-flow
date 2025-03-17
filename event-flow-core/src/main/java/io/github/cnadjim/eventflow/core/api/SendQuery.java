package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.core.domain.ResponseType;

import java.util.concurrent.CompletableFuture;

public interface SendQuery {
     /**
      * Sends a query and expects a response of type R
      * @param query The query to send
      * @param responseType The expected response type
      * @param <QUERY_RESPONSE> The response type
      * @param <QUERY_REQUEST> The query type
      * @return A CompletableFuture containing the response
      */
     <QUERY_RESPONSE, QUERY_REQUEST> CompletableFuture<QUERY_RESPONSE> query(QUERY_REQUEST query, ResponseType<QUERY_RESPONSE> responseType);
}
