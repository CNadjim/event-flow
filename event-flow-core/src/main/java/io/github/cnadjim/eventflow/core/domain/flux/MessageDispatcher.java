package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.Message;
import io.github.cnadjim.eventflow.core.domain.Topic;

public interface MessageDispatcher<MESSAGE extends Message> extends MessagePublisher<MESSAGE> {

    Class<MESSAGE> classOfMessage();

    void dispatch(MESSAGE message);

    default void subscribe(Class<?> messagePayloadClass) {
        final MessageSubscriber<MESSAGE> subscriber = new MessageSubscriber<>() {

            @Override
            public void onSubscribe(Subscription subscription) {

            }

            @Override
            public Topic topic() {
                return Topic.create(messagePayloadClass.getSimpleName());
            }

            @Override
            public Class<MESSAGE> messageClass() {
                return classOfMessage();
            }

            @Override
            public void onNextMessage(MESSAGE message) {
                dispatch(message);
            }
        };

        subscribe(subscriber);
    }
}
