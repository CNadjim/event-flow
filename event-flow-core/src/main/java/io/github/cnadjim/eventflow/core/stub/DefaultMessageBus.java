package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.annotation.Stub;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.flux.Subscription;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Stub
@Slf4j
public class DefaultMessageBus implements MessageBus {

    private final Map<String, CopyOnWriteArrayList<Message>> messagesByTopic = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<MessageSubscriber>> subscribersByTopic = new ConcurrentHashMap<>();

    @Override
    public void publish(Message message) {
        if (message == null) {
            log.warn("Attempted to publish null message");
            return;
        }

        Topic topic = message.topic();
        String topicName = topic.name();

        // Store the message for replay to future subscribers
        messagesByTopic.computeIfAbsent(topicName, k -> new CopyOnWriteArrayList<>()).add(message);

        // Clean up expired messages
        cleanupExpiredMessages(topic);

        // Deliver to current subscribers
        CopyOnWriteArrayList<MessageSubscriber> subscribers = subscribersByTopic.get(topicName);
        if (subscribers != null) {
            for (MessageSubscriber subscriber : subscribers) {
                try {
                    subscriber.onNext(message);
                } catch (Exception e) {
                    log.error("Error delivering message to subscriber: {}", e.getMessage(), e);
                    subscriber.onError(e);
                }
            }
        }
    }

    @Override
    public void subscribe(MessageSubscriber subscriber) {
        if (subscriber == null) {
            log.warn("Attempted to register null subscriber");
            return;
        }

        Topic topic = subscriber.topic();
        String topicName = topic.name();

        // Register the subscriber
        CopyOnWriteArrayList<MessageSubscriber> subscribers = subscribersByTopic.computeIfAbsent(
                topicName, k -> new CopyOnWriteArrayList<>());

        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);

            // Create a subscription that allows unsubscribing
            Subscription subscription = () -> {
                CopyOnWriteArrayList<MessageSubscriber> subs = subscribersByTopic.get(topicName);
                if (subs != null) {
                    subs.remove(subscriber);
                }
            };

            // Notify subscriber about subscription
            subscriber.onSubscribe(subscription);

            // Clean up expired messages before replaying
            cleanupExpiredMessages(topic);

            // Replay stored messages to the new subscriber
            CopyOnWriteArrayList<Message> messages = messagesByTopic.get(topicName);
            if (messages != null) {
                for (Message storedMessage : messages) {
                    try {
                        subscriber.onNext(storedMessage);
                    } catch (Exception e) {
                        log.error("Error replaying message to subscriber: {}", e.getMessage(), e);
                        subscriber.onError(e);
                        break;
                    }
                }
            }
        }
    }

    private void cleanupExpiredMessages(Topic topic) {
        String topicName = topic.name();
        long retentionMs = topic.retentionInMs();

        // If retention is 0 or negative, keep messages indefinitely
        if (retentionMs <= 0) {
            return;
        }

        CopyOnWriteArrayList<Message> messages = messagesByTopic.get(topicName);
        if (messages != null) {
            long now = Instant.now().toEpochMilli();

            // Use iterator to safely remove while iterating
            Iterator<Message> iterator = messages.iterator();

            while (iterator.hasNext()) {
                Message storedMessage = iterator.next();
                if (now - storedMessage.timestamp().toEpochMilli() > retentionMs) {
                    messages.remove(storedMessage);
                }
            }
        }
    }
}
