package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;

/**
 * Service Provider Interface for message bus implementations.
 * The message bus is responsible for publishing messages and managing subscriptions.
 * It acts as a communication channel between different components of the event flow system.
 */
public interface MessageBus {

    /**
     * Publishes a message to all subscribers interested in this message type.
     *
     * @param message The message to publish
     * @param <MESSAGE> The type of the message must extend Message
     */
    <MESSAGE extends Message> void publish(MESSAGE message);

    /**
     * Registers a subscriber to receive messages of a specific type.
     *
     * @param messageSubscriber The subscriber to register
     * @param <MESSAGE> The type of message the subscriber is interested in
     */
    <MESSAGE extends Message> void subscribe(MessageSubscriber<MESSAGE> messageSubscriber);
}
