package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.isNull;

public record DefaultMessageSubscriber(Topic topic,
                                       String handlerName,
                                       Function<Message, Boolean> messageConsumer,
                                       Consumer<Subscription> subscriptionConsumer) implements MessageSubscriber {

    public DefaultMessageSubscriber {
        if (isNull(topic)) throw new BadArgumentException("topic cannot be null");
        if (isNull(messageConsumer)) throw new BadArgumentException("messageConsumer cannot be null");
    }

    public DefaultMessageSubscriber(Topic topic, String handlerName, Function<Message, Boolean> messageConsumer) {
        this(topic, handlerName, messageConsumer, (subscription) -> {
        });
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscriptionConsumer.accept(subscription);
    }

    @Override
    public boolean onNext(Message message) {
        try {
            return messageConsumer.apply(message);
        } catch (Exception exception) {
            return false;
        }
    }
}
