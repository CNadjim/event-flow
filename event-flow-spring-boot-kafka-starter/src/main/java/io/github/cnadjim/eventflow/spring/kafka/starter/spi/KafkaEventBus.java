package io.github.cnadjim.eventflow.spring.kafka.starter.spi;

import io.github.cnadjim.eventflow.core.api.SendEvent;
import io.github.cnadjim.eventflow.core.domain.Event;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaEventBus implements Runnable, EventSubscriber, EventPublisher {

    private final SendEvent sendEvent;
    private final AtomicBoolean running;
    private final KafkaConsumer<String, Event> kafkaConsumer;
    private final KafkaProducer<String, Event> kafkaProducer;

    public KafkaEventBus(final SendEvent sendEvent,
                         final KafkaConsumer<String, Event> kafkaConsumer,
                         final KafkaProducer<String, Event> kafkaProducer) {
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaProducer = kafkaProducer;
        this.running = new AtomicBoolean(true);
        this.sendEvent = sendEvent;
    }

    @Override
    public void publish(Event event) {
        final ProducerRecord<String, Event> producerRecord = new ProducerRecord<>(event.topic(), null, event.timestamp().toEpochMilli(), event.id(), event);
        kafkaProducer.send(producerRecord);
    }

    @Override
    public void publishAll(List<Event> events) {
        for (Event event : events) {
            publish(event);
        }
    }

    @Override
    public void subscribe(String topic) {
        kafkaConsumer.subscribe(Collections.singleton(topic));
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
                    final ConsumerRecords<String, Event> records = kafkaConsumer.poll(Duration.ofMillis(1000));

                    for (ConsumerRecord<String, Event> record : records) {
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
