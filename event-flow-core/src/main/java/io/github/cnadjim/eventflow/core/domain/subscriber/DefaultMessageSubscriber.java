package io.github.cnadjim.eventflow.core.domain.subscriber;

import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.flux.Subscription;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

import java.util.function.Consumer;

import static java.util.Objects.isNull;

public record DefaultMessageSubscriber<MESSAGE extends Message>(Topic topic,
                                                                Class<MESSAGE> messageType,
                                                                Consumer<MESSAGE> messageConsumer,
                                                                Consumer<Subscription> subscriptionConsumer) implements MessageSubscriber<MESSAGE> {

    public DefaultMessageSubscriber {
        if (isNull(topic)) throw new IllegalArgumentException("topic cannot be null");
        if (isNull(messageType)) throw new IllegalArgumentException("messageType cannot be null");
        if (isNull(messageConsumer)) throw new IllegalArgumentException("messageConsumer cannot be null");
        if (isNull(subscriptionConsumer)) throw new IllegalArgumentException("subscriptionConsumer cannot be null");
    }

    public DefaultMessageSubscriber(Topic topic,
                                    Class<MESSAGE> messageType,
                                    Consumer<MESSAGE> messageConsumer) {
        this(topic, messageType, messageConsumer, subscription -> {
        });
    }

    @Override
    public void onNextMessage(MESSAGE message) {
        messageConsumer.accept(message);
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscriptionConsumer.accept(subscription);
    }

    public static <MESSAGE extends Message> DefaultMessageSubscriber<MESSAGE> create(Topic topic,
                                                                                     Class<MESSAGE> messageType,
                                                                                     Consumer<MESSAGE> messageConsumer,
                                                                                     Consumer<Subscription> subscriptionConsumer) {
        if (isNull(subscriptionConsumer)) {
            return new DefaultMessageSubscriber<>(topic, messageType, messageConsumer);
        } else {
            return new DefaultMessageSubscriber<>(topic, messageType, messageConsumer, subscriptionConsumer);
        }
    }
}
