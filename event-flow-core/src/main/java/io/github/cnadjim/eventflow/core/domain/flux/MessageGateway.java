package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import io.github.cnadjim.eventflow.core.domain.exception.RequestTimeoutException;
import io.github.cnadjim.eventflow.core.domain.log.Logger;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.message.MessageResult;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface MessageGateway<MESSAGE extends Message> extends MessagePublisher, MessageConverter<MessageResult>, Logger {

    int TIMEOUT_SECONDS = 30;

    @Override
    default MessageResult convert(Message message) {
        return convert(message, MessageResult.class);
    }

    default CompletableFuture<MessageResult> sendMessage(MESSAGE message) {
        logMessageSent(message);
        publish(message);
        return waitForResponse(message);
    }

    private CompletableFuture<MessageResult> waitForResponse(MESSAGE message) {
        CompletableFuture<MessageResult> future = new CompletableFuture<>();
        subscribeToResults(message, future);
        handleTimeout(message, future);
        return future;
    }

    private void subscribeToResults(MESSAGE message, CompletableFuture<MessageResult> future) {
        MessageSubscriber subscriber = new DefaultMessageSubscriber(
                new MessageResultTopic(message.topic().name()),
                null,
                (nextMessage) -> processResponse(message, nextMessage, future),
                (subscription) -> future.whenComplete((messageResult, throwable) -> subscription.unsubscribe())
        );
        subscribe(subscriber);
    }

    private boolean processResponse(MESSAGE original, Message response, CompletableFuture<MessageResult> future) {
        if (!response.id().equals(original.id())) {
            return false;
        }

        logMessageReceived(original);

        MessageResult result = convert(response);

        if (result.isSuccess()) {
            future.complete(result);
        } else {
            future.completeExceptionally(new EventFlowException(result.error()));
        }

        return true;
    }

    private void handleTimeout(MESSAGE message, CompletableFuture<MessageResult> future) {
        CompletableFuture.delayedExecutor(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .execute(() -> {
                    if (!future.isDone()) {
                        logMessageTimeout(message);
                        future.completeExceptionally(new RequestTimeoutException("Request timed out after " + TIMEOUT_SECONDS + " seconds"));
                    }
                });
    }

    private void logMessageSent(MESSAGE message) {
        logger().debug("[ {} ] [ {} ] Sent", message.id(), message.payloadClassSimpleName());
    }

    private void logMessageReceived(MESSAGE message) {
        logger().debug("[ {} ] [ {} ] Received", message.id(), message.payloadClassSimpleName());
    }

    private void logMessageTimeout(MESSAGE message) {
        logger().debug("[ {} ] [ {} ] Timeout", message.id(), message.payloadClassSimpleName());
    }
}
