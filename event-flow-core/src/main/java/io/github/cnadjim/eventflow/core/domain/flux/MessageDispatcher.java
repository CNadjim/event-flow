package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.message.MessageResult;
import io.github.cnadjim.eventflow.core.domain.topic.MessageTopic;

public interface MessageDispatcher<MESSAGE extends Message, DISPATCH_RESULT> extends MessagePublisher, MessageConverter<MESSAGE> {

    /**
     * Processes a message of the specific type.
     * This method is called when a message needs to be handled by this dispatcher.
     *
     * @param message the message to dispatch and process
     */
    DISPATCH_RESULT dispatch(MESSAGE message);

    Error convert(Throwable exception);

    default void onDispatchStart(Message message){

    }

    default void onDispatchSuccess(Message message){

    }

    default void onDispatchError(Message message, Error error){

    }

    default boolean convertAndDispatch(Message message) {
        try {
            onDispatchStart(message);
            final MESSAGE convertedMessage = convert(message);
            final DISPATCH_RESULT result = dispatch(convertedMessage);
            final MessageResult messageResult = MessageResult.success(message, result);
            publish(messageResult);
            onDispatchSuccess(message);
            return true;
        } catch (Exception exception) {
            final Error error = convert(exception);
            final MessageResult messageResult = MessageResult.failure(message, error);
            publish(messageResult);
            onDispatchError(message, error);
            return true;
        }
    }

    /**
     * Subscribes this dispatcher to a specific message topic.
     * This default implementation creates a DefaultMessageSubscriber that will
     * call the dispatch method when messages are received on the specified topic.
     *
     * @param messageTopic the topic to subscribe to
     */
    default void subscribe(MessageTopic messageTopic) {
        final MessageSubscriber messageSubscriber = new DefaultMessageSubscriber(messageTopic, this::convertAndDispatch);
        subscribe(messageSubscriber);
    }
}
