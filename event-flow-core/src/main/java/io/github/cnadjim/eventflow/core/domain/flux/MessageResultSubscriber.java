package io.github.cnadjim.eventflow.core.domain.flux;


import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.message.MessageResult;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

import java.util.concurrent.CompletableFuture;

/**
 * A specialized subscriber that handles the results of message processing.
 * This interface extends MessageSubscriber to provide functionality for processing
 * success and failure results for a specific message, typically used in request-response patterns.
 *
 * @param <MESSAGE> the type of message for which this subscriber handles results
 */
public interface MessageResultSubscriber<MESSAGE extends Message> extends MessageSubscriber<MessageResult<MESSAGE>> {

    /**
     * Returns the original message for which this subscriber is waiting for a result.
     *
     * @return the original message
     */
    MESSAGE message();

    /**
     * Returns the future that will be completed when a result is received.
     * This future is used to provide asynchronous access to the result.
     *
     * @return the completable future associated with the result
     */
    CompletableFuture<?> future();

    /**
     * Handles a successful result for the message.
     * This method is called when a success result is received for the original message.
     *
     * @param messageResult the successful result to handle
     */
    void handleSuccess(MessageResult<MESSAGE> messageResult);

    /**
     * Handles a failure result for the message.
     * This default implementation completes the future exceptionally with an EventFlowException.
     *
     * @param messageResult the failure result to handle
     */
    default void handleFailure(MessageResult<MESSAGE> messageResult) {
        final EventFlowException eventFlowException = new EventFlowException(messageResult.error());
        future().completeExceptionally(eventFlowException);
    }

    /**
     * Returns the class of the message result type this subscriber can handle.
     * This default implementation returns the MessageResult class.
     *
     * @return the class object representing the message result type
     */
    default Class<MessageResult<MESSAGE>> messageType() {
        //noinspection unchecked
        return (Class<MessageResult<MESSAGE>>) (Class<?>) MessageResult.class;
    }

    /**
     * Returns the topic this subscriber is interested in.
     * This default implementation creates a MessageResultTopic for the original message.
     *
     * @return the topic for receiving results for the original message
     */
    default Topic topic() {
        return MessageResultTopic.create(message());
    }

    /**
     * Called when the subscriber is subscribed to a publisher.
     * This default implementation automatically unsubscribes when the future completes.
     *
     * @param messageSubscription the subscription that represents the connection
     */
    default void onSubscribe(final Subscription messageSubscription) {
        future().whenCompleteAsync((result, throwable) -> messageSubscription.unsubscribe());
    }

    /**
     * Called when a new message result is available.
     * This default implementation checks if the result is for the original message,
     * and if so, delegates to handleSuccess or handleFailure based on the result type.
     *
     * @param messageResult the message result to process
     */
    default void onNextMessage(MessageResult<MESSAGE> messageResult) {
        if (message().id().equals(messageResult.id())) {
            if (messageResult.isSuccess()) {
                handleSuccess(messageResult);
            } else if (messageResult.isFailure()) {
                handleFailure(messageResult);
            }
        }
    }
}
