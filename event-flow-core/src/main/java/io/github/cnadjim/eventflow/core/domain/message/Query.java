package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.supplier.IdSupplier;

import static java.util.Objects.isNull;

/**
 * Represents a query message in the event flow system.
 * A query is a request for information that does not change the state of the system.
 * Queries are typically handled by a single handler and return a response.
 *
 * @param id The unique identifier of the query
 * @param payload The payload of the query, containing the parameters for the query
 */
public record Query(String id,
                    Object payload) implements Message {

    /**
     * Compact constructor for the Query record.
     * Validates that the payload is not null.
     *
     * @throws IllegalArgumentException if payload is null
     */
    public Query {
        if (isNull(payload)) throw new IllegalArgumentException("payload cannot be null");
    }

    /**
     * Creates a new Query with the given payload.
     * The query ID is generated automatically.
     *
     * @param payload The payload of the query
     * @return A new Query instance
     */
    public static Query create(Object payload) {
        return new Query(
                IdSupplier.create(),
                payload
        );
    }
}
