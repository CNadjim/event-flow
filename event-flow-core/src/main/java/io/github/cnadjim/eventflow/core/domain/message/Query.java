package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import io.github.cnadjim.eventflow.core.domain.supplier.IdSupplier;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.isNull;

/**
 * Represents a query message in the event flow system.
 * A query is a request for information that does not change the state of the system.
 * Queries are typically handled by a single handler and return a response.
 *
 * @param id      The unique identifier of the query
 * @param payload The payload of the query, containing the parameters for the query
 */
public record Query(String id,
                    Object payload) implements Message {

    /**
     * Compact constructor for the Query record.
     * Validates that the payload is not null.
     *
     * @throws BadArgumentException if the payload is null or id is blank
     */
    public Query {
        if (StringUtils.isBlank(id)) throw new BadArgumentException("id cannot be null");
        if (isNull(payload)) throw new BadArgumentException("payload cannot be null");
    }

    public Query(Object payload) {
        this(IdSupplier.create(), payload);
    }
}
