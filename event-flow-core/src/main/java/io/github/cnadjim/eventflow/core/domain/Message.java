package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.supplier.MessageTypeSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TopicSupplier;

import java.time.Instant;

/**
 * Representation of a Message, containing a Payload. Typical examples of Messages are Commands, Events and Queries.
 */
public sealed interface Message extends TopicSupplier, PayloadSupplier, MessageTypeSupplier, Comparable<Message> permits Command, Event {

    String id();

    Instant timestamp();

    @Override
    default MessageType type() {
        return switch (this) {
            case Command command -> MessageType.COMMAND;
            case Event event -> MessageType.EVENT;
        };
    }

    @Override
    default int compareTo(Message otherMessage) {
        return timestamp().compareTo(otherMessage.timestamp());
    }
}
