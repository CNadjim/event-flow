package io.github.cnadjim.eventflow.spring.kafka.starter.domain;

import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
@Getter
public class DefaultEventFlowKafkaConsumer implements EventFlowKafkaConsumer {

    private final Topic topic;
    private final Consumer<Void> onShutdown;
    private final KafkaConsumer<String, Message> consumer;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Collection<MessageSubscriber<?>> subscribers = new CopyOnWriteArrayList<>();

    public DefaultEventFlowKafkaConsumer(Topic topic, Consumer<Void> onShutdown, KafkaConsumer<String, Message> consumer) {
        this.topic = topic;
        this.consumer = consumer;
        this.onShutdown = onShutdown;
        this.executorService.submit(this::run);
    }

    @Override
    public void run() {
        log.debug("{} consumer started", topic.name());
        
        try {
            consumer.subscribe(Collections.singleton(topic.name()));

            while (running.get()) {
                final ConsumerRecords<String, Message> records = consumer.poll(Duration.ofMillis(10000));

                for (ConsumerRecord<String, Message> record : records) {
                    final Message message = record.value();
                    sendMessageToSubscriber(message);
                }
            }
        } catch (Exception exception) {
            log.error(ExceptionUtils.getRootCauseMessage(exception), exception);
        } finally {
            consumer.unsubscribe();
            consumer.close();
        }
    }

    @Override
    public void shutdown() {
        try{
            running.set(false);
            executorService.close();
        }catch (Exception exception) {
            log.error("{} consumer shutting down exception", topic.name(), exception);
        } finally {
            onShutdown.accept(null);
            log.debug("{} consumer shutting down", topic.name());
        }
    }
}
