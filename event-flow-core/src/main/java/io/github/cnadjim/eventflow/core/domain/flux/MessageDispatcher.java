
package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.log.Logger;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.message.MessageResult;
import io.github.cnadjim.eventflow.core.domain.topic.MessageTopic;

public interface MessageDispatcher<MESSAGE extends Message, RESULT> extends MessagePublisher, MessageConverter<MESSAGE>, Logger {

    RESULT dispatch(MESSAGE message);

    Error convert(Throwable exception);

    default boolean processMessage(Message message) {
        logStart(message);

        try {
            MESSAGE convertedMessage = convert(message);
            RESULT result = dispatch(convertedMessage);

            publishSuccess(message, result);
            logSuccess(message);

        } catch (Exception e) {
            Error error = convert(e);
            publishError(message, error);
            logError(message, error);
        }

        return true;
    }

    default void subscribeTo(MessageTopic topic, String handlerName) {
        MessageSubscriber subscriber = new DefaultMessageSubscriber(
                topic,
                handlerName,
                this::processMessage
        );
        subscribe(subscriber);
    }

    private void publishSuccess(Message message, RESULT result) {
        MessageResult messageResult = MessageResult.success(message, result);
        publish(messageResult);
    }

    private void publishError(Message message, Error error) {
        MessageResult messageResult = MessageResult.failure(message, error);
        publish(messageResult);
    }

    private void logStart(Message message) {
        logger().debug("[ {} ] [ {} ] Processing started",
                message.id(), message.payloadClassSimpleName());
    }

    private void logSuccess(Message message) {
        logger().debug("[ {} ] [ {} ] Processing completed",
                message.id(), message.payloadClassSimpleName());
    }

    private void logError(Message message, Error error) {
        logger().debug("[ {} ] [ {} ] Processing failed: {}",
                message.id(), message.payloadClassSimpleName(), error.message());
    }
}
