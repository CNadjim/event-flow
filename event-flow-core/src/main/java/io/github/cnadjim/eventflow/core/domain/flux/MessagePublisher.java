package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.message.Message;

/**
 * A publisher of messages that allows subscribers to register for receiving messages.
 * This interface is the entry point for the publisher-subscriber pattern implementation
 * for message distribution.
 *
 * @param <MESSAGE> the type of messages this publisher can publish, must extend Message
 */
public interface MessagePublisher<MESSAGE extends Message> {

    /**
     * Registers a subscriber to receive messages from this publisher.
     * Once subscribed, the subscriber will receive messages of the specified type
     * whenever they are published.
     *
     * @param subscriber the subscriber to register for receiving messages
     */
    void subscribe(MessageSubscriber<MESSAGE> subscriber);
}
