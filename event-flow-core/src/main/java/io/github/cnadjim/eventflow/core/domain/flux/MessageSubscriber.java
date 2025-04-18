package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

/**
 * A specialized subscriber that receives messages of a specific type.
 * This interface extends the base Subscriber interface to provide type-safe
 * message handling and topic-based subscription.
 *
 * @param <MESSAGE> the specific type of message this subscriber can handle
 */
public interface MessageSubscriber<MESSAGE extends Message> extends Subscriber<Message> {

    /**
     * Returns the topic this subscriber is interested in.
     * Messages published to this topic will be delivered to this subscriber.
     *
     * @return the topic this subscriber is subscribed to
     */
    Topic topic();

    /**
     * Returns the class of the message type this subscriber can handle.
     * This is used for type conversion when receiving generic messages.
     *
     * @return the class object representing the specific message type
     */
    Class<MESSAGE> messageType();

    /**
     * Called when a new message of the specific type is available.
     * This method is invoked after the message has been converted to the correct type.
     *
     * @param message the typed message to process
     */
    void onNextMessage(MESSAGE message);

    /**
     * Called when a new message is available from the publisher.
     * This default implementation converts the generic message to the specific type
     * and delegates to onNextMessage.
     *
     * @param message the generic message from the publisher
     */
    default void onNext(Message message) {
        final MESSAGE convertedMessage = Message.convert(message, messageType());
        onNextMessage(convertedMessage);
    }

    /**
     * Called when an error occurs during processing.
     * This default implementation does nothing and can be overridden by subclasses.
     *
     * @param throwable the error that occurred
     */
    default void onError(Throwable throwable) {

    }

    /**
     * Called when the publisher has no more messages to emit.
     * This default implementation does nothing and can be overridden by subclasses.
     */
    default void onComplete() {

    }


}
