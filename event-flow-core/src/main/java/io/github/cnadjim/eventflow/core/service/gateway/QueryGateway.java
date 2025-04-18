package io.github.cnadjim.eventflow.core.service.gateway;

import io.github.cnadjim.eventflow.annotation.DomainService;
import io.github.cnadjim.eventflow.core.api.SendQuery;
import io.github.cnadjim.eventflow.core.domain.message.Query;
import io.github.cnadjim.eventflow.core.domain.response.ResponseType;
import io.github.cnadjim.eventflow.core.domain.subscriber.QueryResultSubscriber;
import io.github.cnadjim.eventflow.core.spi.MessageBus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * {@code QueryGateway} is a domain service that acts as a gateway for sending queries to the system.
 * It implements the {@link SendQuery} interface and uses a {@link MessageBus} for sending and receiving query messages.
 * It provides a method to asynchronously send a query and obtain its result via a {@link CompletableFuture}.
 */
@DomainService
public class QueryGateway implements SendQuery {

    private final MessageBus messageBus;

    /**
     * Constructs a {@code QueryGateway} with the necessary {@link MessageBus} dependency.
     *
     * @param messageBus The {@link MessageBus} used for sending and receiving query messages.
     */
    public QueryGateway(final MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    /**
     * Sends a query to the system asynchronously.
     * It creates a {@link Query} message, subscribes a {@link QueryResultSubscriber} to the message bus to receive the query result,
     * publishes the query message, and returns a {@link CompletableFuture} that will be completed with the query result.
     * The type of the expected result is specified using a {@link ResponseType}. The future is configured with a timeout of 1 minute.
     *
     * @param queryPayload        The query object to send.
     * @param responseType The {@link ResponseType} representing the expected type of the query result.
     * @param <QUERY_RESPONSE> The type of the query response.
     * @param <QUERY_REQUEST> The type of the query request.
     * @return A {@link CompletableFuture} that will be completed with the query result.
     */
    @Override
    public <QUERY_RESPONSE, QUERY_REQUEST> CompletableFuture<QUERY_RESPONSE> send(QUERY_REQUEST queryPayload, ResponseType<QUERY_RESPONSE> responseType) {
        final CompletableFuture<QUERY_RESPONSE> resultFuture = new CompletableFuture<>();
        final Query queryMessage = new Query(queryPayload);
        final QueryResultSubscriber<QUERY_RESPONSE> queryResultObserver = new QueryResultSubscriber<>(queryMessage, responseType, resultFuture);

        messageBus.subscribe(queryResultObserver);
        messageBus.publish(queryMessage);

        return resultFuture.orTimeout(1, TimeUnit.MINUTES);
    }

}
