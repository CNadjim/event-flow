package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.message.Message;

/**
 * A publisher of messages that allows subscribers to register for receiving messages.
 * This interface is the entry point for the publisher-subscriber pattern implementation
 * for message distribution.
 */
public interface MessagePublisher extends Publisher<MessageSubscriber> {

    void publish(Message message);
}
