package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowException;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.message.MessageResult;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface MessageGateway<MESSAGE extends Message> extends MessagePublisher, MessageConverter<MessageResult> {

    @Override
    default MessageResult convert(Message message) {
        return convert(message, MessageResult.class);
    }


    default void onSuccess(MESSAGE message){

    }

    default void onError(MESSAGE message, Error error){

    }

    default CompletableFuture<MessageResult> sendAndSubscribe(MESSAGE message) {
        publish(message);
        return subscribeToResult(message);
    }

    default CompletableFuture<MessageResult> subscribeToResult(MESSAGE message) {
        final Topic topic = message.topic();
        final CompletableFuture<MessageResult> completableFuture = new CompletableFuture<>();
        final MessageSubscriber messageSubscriber = new DefaultMessageSubscriber(
                new MessageResultTopic(topic.name()),
                (nextMessage) -> {
                    if (nextMessage.id().equals(message.id())) {
                        final MessageResult messageResult = convert(nextMessage);
                        if (messageResult.isSuccess()) {
                            onSuccess(message);
                            completableFuture.complete(messageResult);
                        } else {
                            final Error error = messageResult.error();
                            final EventFlowException exception = new EventFlowException(error);
                            onError(message, error);
                            completableFuture.completeExceptionally(exception);
                        }
                        return true;
                    } else {
                        return false;
                    }
                },
                (subscription) -> completableFuture.whenCompleteAsync((result, error) -> subscription.unsubscribe()));
        subscribe(messageSubscriber);
        return completableFuture.orTimeout(30, TimeUnit.SECONDS);
    }
}
