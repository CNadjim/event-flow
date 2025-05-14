package io.github.cnadjim.eventflow.core.port;

import io.github.cnadjim.eventflow.core.domain.flux.MessagePublisher;

/**
 * Service Provider Interface for message bus implementations.
 * The message bus is responsible for publishing messages and managing subscriptions.
 * It acts as a communication channel between different components of the event flow system.
 */
public interface MessageBus extends MessagePublisher {

}
