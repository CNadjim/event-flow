package io.github.cnadjim.eventflow.spring.rabbitmq.starter.domain;

import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.flux.Subscription;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import java.util.Collection;

public interface EventFlowRabbitMqConsumer {

    void run();

    void shutdown();

    Topic getTopic();

    Collection<MessageSubscriber<?>> getSubscribers();

    ConnectionFactory getConnectionFactory();

    default void addSubscriber(MessageSubscriber<?> messageSubscriber) {
        getSubscribers().add(messageSubscriber);
        messageSubscriber.onSubscribe(new Subscription() {
            @Override
            public void unsubscribe() {
                getSubscribers().remove(messageSubscriber);
                if (getSubscribers().isEmpty()) {
                    shutdown();
                }
            }
        });
    }

    default void sendMessageToSubscriber(Message message) {
        getSubscribers().forEach(subscriber -> subscriber.onNext(message));
    }
}
