package io.github.cnadjim.eventflow.core.domain.flux;


import io.github.cnadjim.eventflow.core.domain.Message;
import io.github.cnadjim.eventflow.core.domain.MessageResult;
import io.github.cnadjim.eventflow.core.domain.Topic;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;

import java.util.concurrent.CompletableFuture;

public interface MessageResultSubscriber<MESSAGE extends Message> extends MessageSubscriber<MessageResult> {

    MESSAGE message();

    CompletableFuture<?> future();

    void handleSuccess(MessageResult messageResult);

    default void handleFailure(MessageResult messageResult) {
        final EventFlowException eventFlowException = new EventFlowException(messageResult.error());
        future().completeExceptionally(eventFlowException);
    }

    default Class<MessageResult> messageClass() {
        return MessageResult.class;
    }

    default Topic topic() {
        return MessageResult.resultTopic(message());
    }

    default void onSubscribe(final Subscription messageSubscription) {
        future().whenCompleteAsync((result, throwable) -> messageSubscription.unsubscribe());
    }

    default void onNextMessage(MessageResult messageResult) {
        if (message().id().equals(messageResult.id())) {
            switch (messageResult.status()) {
                case SUCCESS:
                    handleSuccess(messageResult);
                    break;
                case FAILURE:
                    handleFailure(messageResult);
                    break;
            }
        }
    }
}
