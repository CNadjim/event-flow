package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.IdSupplier;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.isNull;

/**
 * Represents a command message in the event flow system.
 * A command is a request to perform an action or change the state of an aggregate.
 * Commands are typically handled by a single handler and can produce events.
 *
 * @param id          The unique identifier of the command
 * @param payload     The payload of the command, containing the data needed to process it
 * @param aggregateId The identifier of the aggregate that this command targets
 */
public record Command(String id,
                      Object payload,
                      String aggregateId) implements Message, AggregateIdSupplier {

    /**
     * Compact constructor for the Command record.
     * Validates that the payload is not null and the aggregateId is not empty.
     *
     * @throws IllegalArgumentException if payload is null or aggregateId is empty
     */
    public Command {
        if (StringUtils.isBlank(id)) throw new IllegalArgumentException("id cannot be null");
        if (isNull(payload)) throw new IllegalArgumentException("payload cannot be null");
        if (StringUtils.isBlank(aggregateId)) throw new IllegalArgumentException("aggregateId cannot be empty");
    }

    public Command(Object payload) {
        this(IdSupplier.create(), payload, AggregateIdSupplier.getAggregateId(payload));
    }
}
