package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.Message;
import io.github.cnadjim.eventflow.core.domain.Topic;

public interface MessageSubscriber<MESSAGE extends Message> extends Subscriber<Message> {

    Topic topic();

    Class<MESSAGE> messageClass();

    void onNextMessage(MESSAGE message);

    default void onNext(Message message) {
        final MESSAGE convertedMessage = Message.convert(message, messageClass());
        onNextMessage(convertedMessage);
    }

    default void onError(Throwable throwable) {

    }

    default void onComplete() {

    }


}
