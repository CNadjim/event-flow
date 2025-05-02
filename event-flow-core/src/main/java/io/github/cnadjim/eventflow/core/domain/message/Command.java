package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.IdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TimestampSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TopicSupplier;
import io.github.cnadjim.eventflow.core.domain.topic.MessageTopic;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

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
                      Instant timestamp,
                      String aggregateId,
                      MessageTopic topic) implements Message, AggregateIdSupplier {

    /**
     * Compact constructor for the Command record.
     * Validates that the payload is not null and the aggregateId is not empty.
     *
     * @throws BadArgumentException if payload is null or aggregateId is empty
     */
    public Command {
        if (isNull(topic)) throw new BadArgumentException("topic cannot be null");
        if (isNull(payload)) throw new BadArgumentException("payload cannot be null");
        if (StringUtils.isBlank(id)) throw new BadArgumentException("id cannot be null");
        if (StringUtils.isBlank(aggregateId)) throw new BadArgumentException("aggregateId cannot be empty");
    }

    public Command(Object payload) {
        this(IdSupplier.create(), payload, TimestampSupplier.create(), AggregateIdSupplier.getAggregateId(payload), TopicSupplier.create(payload));
    }
}
