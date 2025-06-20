package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import io.github.cnadjim.eventflow.core.domain.supplier.IdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TimestampSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TopicSupplier;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

import java.io.Serializable;
import java.time.Instant;


public interface Message extends IdSupplier, PayloadSupplier, TimestampSupplier, TopicSupplier, Serializable, Comparable<Message> {

    String id();

    Object payload();

    Topic topic();

    Instant timestamp();

    @Override
    default int compareTo(Message message) {
        return timestamp().compareTo(message.timestamp());
    }

    static <MESSAGE extends Message> MESSAGE convert(Message message, Class<MESSAGE> messageClass) {
        if (messageClass.isInstance(message)) {
            return messageClass.cast(message);
        } else throw new BadArgumentException("Cannot convert message to a targetType of " + messageClass.getName());
    }
}
