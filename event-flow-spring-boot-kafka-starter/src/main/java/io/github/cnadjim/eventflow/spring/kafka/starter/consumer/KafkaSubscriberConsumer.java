package io.github.cnadjim.eventflow.spring.kafka.starter.consumer;

import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.flux.Subscription;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.spring.kafka.starter.kafka.KafkaMessageDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class KafkaSubscriberConsumer implements Runnable {

    private final MessageSubscriber subscriber;
    private final KafkaConsumer<String, Message> kafkaConsumer;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public KafkaSubscriberConsumer(MessageSubscriber subscriber,
                                   Properties messageConsumerConfig,
                                   KafkaMessageDeserializer kafkaMessageDeserializer) {
        this.subscriber = subscriber;
        this.kafkaConsumer = new KafkaConsumer<>(messageConsumerConfig, new StringDeserializer(), kafkaMessageDeserializer);
        this.kafkaConsumer.subscribe(Collections.singletonList(subscriber.topic().name()));
        executorService.submit(this);
    }

    @Override
    public void run() {
        running.set(true);
        final Subscription subscription = this::shutdown;
        subscriber.onSubscribe(subscription);

        try {
            while (running.get()) {
                try {
                    ConsumerRecords<String, Message> records = kafkaConsumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord<String, Message> record : records) {
                        boolean ack = subscriber.onNext(record.value());
                        if (ack) {
                            // If message was successfully processed, commit the offset
                            kafkaConsumer.commitSync();
                        } else {
                            // If message was not successfully processed, seek to the current position
                            // This will cause the message to be redelivered in the next poll
                            kafkaConsumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset());
                        }
                    }
                } catch (WakeupException e) {
                    // Ignore exception if closing
                    if (running.get()) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    log.error("Error while consuming messages", e);
                    subscriber.onError(e);
                }
            }
        } finally {
            try {
                kafkaConsumer.close();
                subscriber.onComplete();
            } catch (Exception e) {
                log.error("Error while closing Kafka consumer", e);
            }
        }
    }

    public void shutdown() {
        running.set(false);
        kafkaConsumer.wakeup();
        executorService.shutdown();
    }
}
