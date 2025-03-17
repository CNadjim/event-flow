package io.github.cnadjim.eventflow.spring.kafka.starter.config;

import io.github.cnadjim.eventflow.core.api.SendEvent;
import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.spring.kafka.starter.spi.KafkaEventBus;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;

public class EventFlowConfig {

    @Bean
    public KafkaEventBus kafkaEventBus(final SendEvent sendEvent,
                                       final KafkaConsumer<String, Event> eventConsumer,
                                       final KafkaProducer<String, Event> eventProducer) {
        return new KafkaEventBus(sendEvent, eventConsumer, eventProducer);
    }
}
