package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import io.github.cnadjim.eventflow.core.domain.exception.RequestTimeoutException;
import io.github.cnadjim.eventflow.core.domain.log.Logger;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.message.MessageResult;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

public interface MessageGateway<MESSAGE extends Message> extends MessagePublisher, MessageConverter<MessageResult>, Logger {

    Integer TIMEOUT_IN_SECOND = 30;

    @Override
    default MessageResult convert(Message message) {
        return convert(message, MessageResult.class);
    }

    default void onReceived(MESSAGE message) {
        logger().debug("[ {} ] [ {} ] Requested", message.id(), message.payloadClassSimpleName());
    }

    default void onResultReceived(MESSAGE message) {
        logger().debug("[ {} ] [ {} ] Responded", message.id(), message.payloadClassSimpleName());
    }

    default void onResultTimeout(MESSAGE message) {
        logger().debug("[ {} ] [ {} ] Timed out", message.id(), message.payloadClassSimpleName());
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
            return false;
        }
    }

    default CompletableFuture<MessageResult> sendAndSubscribe(MESSAGE message) {
        onReceived(message);
        publish(message);
        return subscribeToResult(message);
    }

    default  <T> CompletableFuture<T> withRequestTimeOutException(CompletableFuture<T> future, MESSAGE message) {

        CompletableFuture<T> timeoutFuture = new CompletableFuture<>();

        CompletableFuture.delayedExecutor(TIMEOUT_IN_SECOND, TimeUnit.SECONDS).execute(() -> {
            if (!future.isDone()) {
                onResultTimeout(message);
                final RequestTimeoutException requestTimeoutException = new RequestTimeoutException(String.format("The %s second timeout has expired without result.", TIMEOUT_IN_SECOND));
                timeoutFuture.completeExceptionally(requestTimeoutException);
            }
        });

        future.whenComplete((result, exception) -> {
            if (nonNull(exception)) {
                timeoutFuture.completeExceptionally(exception);
            } else {
                timeoutFuture.complete(result);
            }
        });

        return timeoutFuture;
    }

    default CompletableFuture<MessageResult> subscribeToResult(MESSAGE message) {
        final Topic topic = message.topic();
        final CompletableFuture<MessageResult> completableFuture = new CompletableFuture<>();
        final MessageSubscriber messageSubscriber = new DefaultMessageSubscriber(
                new MessageResultTopic(topic.name()),
                (nextMessage) -> consumeNextMessage(message, nextMessage, completableFuture),
                (subscription) -> completableFuture.whenCompleteAsync((result, error) -> subscription.unsubscribe()));
        subscribe(messageSubscriber);
        return withRequestTimeOutException(completableFuture, message);
    }
}
