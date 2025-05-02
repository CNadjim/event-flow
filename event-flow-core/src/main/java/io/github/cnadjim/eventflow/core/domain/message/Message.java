package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import io.github.cnadjim.eventflow.core.domain.supplier.IdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TimestampSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.TopicSupplier;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

import java.io.Serializable;
import java.time.Instant;

/**
 * {@code Message} is a sealed interface representing a generic message within the event flow core.
 * It extends several supplier interfaces to provide access to message attributes such as ID, payload, and topic.
 * It is {@code Serializable} to allow for message persistence and transport.
 * <p>
 */
public interface Message extends IdSupplier, PayloadSupplier, TimestampSupplier, TopicSupplier, Serializable {

    /**
     * Returns the unique identifier of the message.
     *
     * @return The message ID.
     */
    String id();

    /**
     * Returns the payload of the message. The payload represents the data being carried by the message.
     *
     * @return The message payload.
     */
    Object payload();

    /**
     * Returns the topic associated with the message.  The default implementation creates a {@link Topic}
     * based on the simple name of the payload class.
     *
     * @return The message topic.
     */
    Topic topic();

    Instant timestamp();

    /**
     * Converts a message to a specific target type.
     *
     * @param message      The message to convert.
     * @param messageClass The class of the target type.
     * @param <MESSAGE>    The type parameter representing the target message type.
     * @return The converted message, cast to the target type.
     * @throws BadArgumentException if the message cannot be converted to the target type.
     */
    static <MESSAGE extends Message> MESSAGE convert(Message message, Class<MESSAGE> messageClass) {
        if (messageClass.isInstance(message)) {
            return messageClass.cast(message);
        } else throw new BadArgumentException("Cannot convert message to a targetType of " + messageClass.getName());
    }
}
