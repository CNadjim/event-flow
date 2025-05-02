package io.github.cnadjim.eventflow.core.api;

import io.github.cnadjim.eventflow.annotation.UseCase;
import io.github.cnadjim.eventflow.core.domain.message.Query;
import io.github.cnadjim.eventflow.core.domain.response.ResponseType;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for sending queries in the event flow system.
 * Queries are requests for information that do not change the state of the system.
 */
@UseCase
public interface SendQuery {

    /**
     * Sends a query to be processed by the appropriate handler.
     *
     * @param query            The query request object to be processed
     * @param responseType     The expected response type for the query
     * @param <QUERY_RESPONSE> The type of the response
     * @return A CompletableFuture containing the query response
     */
    <QUERY_RESPONSE> CompletableFuture<QUERY_RESPONSE> send(Query query, ResponseType<QUERY_RESPONSE> responseType);

    default <QUERY_RESPONSE> CompletableFuture<QUERY_RESPONSE> send(Object payload, ResponseType<QUERY_RESPONSE> responseType) {
        return send(new Query(payload), responseType);
    }
}
