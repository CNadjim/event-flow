package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.exception.field.AggregateIdMissingException;
import io.github.cnadjim.eventflow.core.domain.exception.field.IdMissingException;
import io.github.cnadjim.eventflow.core.domain.exception.field.PayloadMissingException;
import io.github.cnadjim.eventflow.core.domain.exception.field.TopicMissingException;
import io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TopicSupplier;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.UUID;

import static java.util.Objects.isNull;

public record Command(String id,
                      String topic,
                      Object payload,
                      Instant timestamp,
                      String aggregateId) implements Message, AggregateIdSupplier {

    public Command {
        if (isNull(payload)) throw new PayloadMissingException();
        if (StringUtils.isBlank(id)) throw new IdMissingException();
        if (StringUtils.isBlank(topic)) throw new TopicMissingException();
        if (StringUtils.isBlank(aggregateId)) throw new AggregateIdMissingException();
    }

    public static Command create(Object payload) {
        return new Command(
                UUID.randomUUID().toString(),
                TopicSupplier.findTopic(payload).orElse(null),
                payload,
                Instant.now(),
                AggregateIdSupplier.findAggregateId(payload).orElse(null)
        );
    }
}
