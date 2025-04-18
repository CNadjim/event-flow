package io.github.cnadjim.eventflow.core.domain.flux;

/**
 * Represents a subscription between a publisher and a subscriber.
 * This functional interface provides a way to cancel the subscription when it's no longer needed.
 */
@FunctionalInterface
public interface Subscription {

    /**
     * Cancels the subscription, indicating that the subscriber is no longer interested
     * in receiving items from the publisher.
     */
    void unsubscribe();
}
