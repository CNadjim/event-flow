package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import io.github.cnadjim.eventflow.core.domain.log.Logger;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.message.MessageResult;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface MessageGateway<MESSAGE extends Message> extends MessagePublisher, MessageConverter<MessageResult>, Logger {

    @Override
    default MessageResult convert(Message message) {
        return convert(message, MessageResult.class);
    }

    default void onReceived(MESSAGE message) {
        logger().debug("[ {} ] Received {} {}", message.id(), message.getClass().getSimpleName(), message.payloadClassSimpleName());
    }

    default void onResultReceived(MESSAGE message) {
        logger().debug("[ {} ] Received {} {} result", message.id(), message.getClass().getSimpleName(), message.payloadClassSimpleName());
    }

    default boolean consumeNextMessage(MESSAGE message, Message nextMessage, CompletableFuture<MessageResult> completableFuture) {
        if (nextMessage.id().equals(message.id())) {
            final MessageResult messageResult = convert(nextMessage);

            onResultReceived(message);

            if (messageResult.isSuccess()) {
                completableFuture.complete(messageResult);
            } else {
                final Error error = messageResult.error();
                final EventFlowException exception = new EventFlowException(error);
                completableFuture.completeExceptionally(exception);
            }

            return true;
        } else {
            logger().debug("[ {} ] Ignoring {} {}", nextMessage.id(), nextMessage.getClass().getSimpleName(), nextMessage.payloadClassSimpleName());
            return false;
        }
    }

    default CompletableFuture<MessageResult> sendAndSubscribe(MESSAGE message) {
        onReceived(message);
        publish(message);
        return subscribeToResult(message);
    }

    default CompletableFuture<MessageResult> subscribeToResult(MESSAGE message) {
        final Topic topic = message.topic();
        final CompletableFuture<MessageResult> completableFuture = new CompletableFuture<>();
        final MessageSubscriber messageSubscriber = new DefaultMessageSubscriber(
                new MessageResultTopic(topic.name()),
                (nextMessage) -> consumeNextMessage(message, nextMessage, completableFuture),
                (subscription) -> completableFuture.whenCompleteAsync((result, error) -> subscription.unsubscribe()));
        subscribe(messageSubscriber);
        return completableFuture.orTimeout(30, TimeUnit.SECONDS);
    }
}
