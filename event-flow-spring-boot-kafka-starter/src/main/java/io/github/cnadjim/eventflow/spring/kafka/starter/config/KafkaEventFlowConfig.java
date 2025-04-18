package io.github.cnadjim.eventflow.spring.kafka.starter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.spring.kafka.starter.service.DefaultKafkaService;
import io.github.cnadjim.eventflow.spring.kafka.starter.service.KafkaService;
import io.github.cnadjim.eventflow.spring.kafka.starter.spi.KafkaMessageBus;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Properties;

@Configuration
public class KafkaEventFlowConfig {

    @Bean
    public KafkaMessageBus kafkaEventBus(final KafkaService kafkaService,
                                         final KafkaProducer<String, Message> messageProducer) {
        return new KafkaMessageBus(kafkaService, messageProducer);
    }


    @Bean
    public KafkaService kafkaService(final KafkaAdmin kafkaAdmin,
                                     @Qualifier("kafkaObjectMapper") final ObjectMapper kafkaObjectMapper,
                                     @Qualifier("messageConsumerConfig") final Properties messageConsumerConfig,
                                     @Qualifier("messageResultConsumerConfig") final Properties messageResultConsumerConfig) {
        return new DefaultKafkaService(kafkaAdmin, kafkaObjectMapper, messageConsumerConfig, messageResultConsumerConfig);
    }
}
