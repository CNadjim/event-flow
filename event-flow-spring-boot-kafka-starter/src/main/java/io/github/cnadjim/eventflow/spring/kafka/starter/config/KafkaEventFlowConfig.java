package io.github.cnadjim.eventflow.spring.kafka.starter.config;

import io.github.cnadjim.eventflow.core.domain.Message;
import io.github.cnadjim.eventflow.spring.kafka.starter.service.KafkaService;
import io.github.cnadjim.eventflow.spring.kafka.starter.spi.KafkaMessageBus;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaEventFlowConfig {

    @Bean
    public KafkaMessageBus kafkaEventBus(final KafkaService kafkaService,
                                         final KafkaConsumer<String, Message> messageConsumer,
                                         final KafkaProducer<String, Message> messageProducer) {
        return new KafkaMessageBus(kafkaService, messageProducer);
    }
}
