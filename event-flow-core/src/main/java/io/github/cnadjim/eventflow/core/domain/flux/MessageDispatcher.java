package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.subscriber.DefaultMessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.topic.MessageTopic;

/**
 * A component that both publishes and dispatches messages of a specific type.
 * This interface extends MessagePublisher to provide additional functionality
 * for dispatching messages and subscribing to topics.
 *
 * @param <MESSAGE> the type of messages this dispatcher can handle, must extend Message
 */
public interface MessageDispatcher<MESSAGE extends Message> extends MessagePublisher<MESSAGE> {

    /**
     * Returns the class of the message type this dispatcher can handle.
     * This is used for type conversion when receiving generic messages.
     *
     * @return the class object representing the specific message type
     */
    Class<MESSAGE> dispatchMessageType();

    /**
     * Processes a message of the specific type.
     * This method is called when a message needs to be handled by this dispatcher.
     *
     * @param message the message to dispatch and process
     */
    void dispatch(MESSAGE message);

    /**
     * Subscribes this dispatcher to a specific message topic.
     * This default implementation creates a DefaultMessageSubscriber that will
     * call the dispatch method when messages are received on the specified topic.
     *
     * @param messageTopic the topic to subscribe to
     */
    default void subscribe(MessageTopic messageTopic) {
        final DefaultMessageSubscriber<MESSAGE> messageSubscriber = DefaultMessageSubscriber.create(messageTopic, dispatchMessageType(), this::dispatch, null);
        subscribe(messageSubscriber);
    }
}
