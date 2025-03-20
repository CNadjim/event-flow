package io.github.cnadjim.eventflow.spring.kafka.starter.spi;

import io.github.cnadjim.eventflow.core.api.SendEvent;
import io.github.cnadjim.eventflow.core.domain.EventWrapper;
import io.github.cnadjim.eventflow.core.spi.EventPublisher;
import io.github.cnadjim.eventflow.core.spi.EventSubscriber;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaEventBus implements Runnable, EventSubscriber, EventPublisher {

    private final SendEvent sendEvent;
    private final AtomicBoolean running;
    private final Set<String> subscribedTopics = new ConcurrentSkipListSet<>();
    private final KafkaConsumer<String, EventWrapper> kafkaConsumer;
    private final KafkaProducer<String, EventWrapper> kafkaProducer;

    public KafkaEventBus(final SendEvent sendEvent,
                         final KafkaConsumer<String, EventWrapper> kafkaConsumer,
                         final KafkaProducer<String, EventWrapper> kafkaProducer) {
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaProducer = kafkaProducer;
        this.running = new AtomicBoolean(true);
        this.sendEvent = sendEvent;
    }

    @Override
    public void publish(EventWrapper event) {
        final ProducerRecord<String, EventWrapper> producerRecord = new ProducerRecord<>(event.topic(), null, event.timestamp().toEpochMilli(), event.id(), event);
        kafkaProducer.send(producerRecord);
    }

    @Override
    public void publishAll(List<EventWrapper> events) {
        for (EventWrapper event : events) {
            publish(event);
        }
    }

    @Override
    public void subscribe(String topic) {
        subscribedTopics.add(topic);
        kafkaConsumer.subscribe(subscribedTopics);
    }

    @Override
    public void start() {
        new Thread(this).start();
    }

    @Override
    public void stop() {
        this.running.set(false);
        kafkaConsumer.close();
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                if (CollectionUtils.isNotEmpty(kafkaConsumer.subscription())) {
                    final ConsumerRecords<String, EventWrapper> records = kafkaConsumer.poll(Duration.ofMillis(1000));

                    for (ConsumerRecord<String, EventWrapper> record : records) {
                        sendEvent.send(record.value());
                    }

                    kafkaConsumer.commitSync();
                }
            }
        } catch (Exception exception) {
            //log.error(ExceptionUtils.getRootCauseMessage(exception), exception);
        } finally {
            kafkaConsumer.close();
        }

    }
}
