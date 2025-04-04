package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.supplier.IdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TopicSupplier;

import java.io.Serializable;

public sealed interface Message extends IdSupplier, PayloadSupplier, TopicSupplier, Serializable permits Aggregate, Command, Event, Query, MessageResult {

    String id();

    Object payload();

    @Override
    default Topic topic() {
        return Topic.create(payloadClassSimpleName());
    }

    static <MESSAGE extends Message> MESSAGE convert(Message message, Class<MESSAGE> targetType) {
        if (targetType.isInstance(message)) {
            return targetType.cast(message);
        } else throw new IllegalArgumentException("Cannot convert message to a targetType of " + targetType.getName());
    }
}
