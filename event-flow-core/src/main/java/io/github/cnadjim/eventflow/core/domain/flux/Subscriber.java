package io.github.cnadjim.eventflow.core.domain.flux;

/**
 * A subscriber of items that follows the Reactive Streams pattern.
 * This interface defines the core methods that every subscriber must implement
 * to receive and process items from a publisher.
 *
 * @param <ITEM> the type of items this subscriber can receive
 */
public interface Subscriber<ITEM> {

    /**
     * Called when the subscriber is subscribed to a publisher.
     * This method provides the subscription that can be used to control the flow
     * or cancel the subscription.
     *
     * @param subscription the subscription that represents the connection between publisher and subscriber
     */
    void onSubscribe(Subscription subscription);

    /**
     * Called when a new item is available from the publisher.
     *
     * @param item the item emitted by the publisher
     */
    void onNext(ITEM item);

    /**
     * Called when an error occurs during processing.
     *
     * @param throwable the error that occurred
     */
    void onError(Throwable throwable);

    /**
     * Called when the publisher has no more items to emit.
     */
    void onComplete();
}
