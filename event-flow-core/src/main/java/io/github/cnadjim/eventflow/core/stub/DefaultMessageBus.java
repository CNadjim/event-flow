package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.annotation.Stub;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.flux.Subscription;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Stub
@Slf4j
public class DefaultMessageBus implements MessageBus {

    private final ConcurrentMap<String, List<MessageSubscriber<?>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public <MESSAGE extends Message> void publish(MESSAGE message) {
        final String topic = message.topic().name();
        final List<MessageSubscriber<?>> topicSubscribers = subscribers.getOrDefault(topic, Collections.emptyList());
        topicSubscribers.forEach(subscriber -> subscriber.onNext(message));
    }

    @Override
    public <MESSAGE extends Message> void subscribe(MessageSubscriber<MESSAGE> messageSubscriber) {
        final String topic = messageSubscriber.topic().name();
        final List<MessageSubscriber<?>> topicSubscribers = subscribers.computeIfAbsent(topic, newTopic -> new CopyOnWriteArrayList<>());
        final Subscription subscription = () -> topicSubscribers.remove(messageSubscriber);
        messageSubscriber.onSubscribe(subscription);
        topicSubscribers.add(messageSubscriber);
    }
}
