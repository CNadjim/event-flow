package io.github.cnadjim.eventflow.spring.kafka.starter.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.spring.kafka.starter.kafka.KafkaMessageDeserializer;
import io.github.cnadjim.eventflow.spring.kafka.starter.kafka.KafkaMessageSerializer;
import io.github.cnadjim.eventflow.spring.starter.property.EventFlowProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

public class KafkaConfig {

    @Value("${spring.application.name}")
    private String springApplicationName;

    @Bean("eventConsumerConfig")
    public Properties eventConsumerConfig(final EventFlowProperties eventFlowProperties) {
        final Properties consumerConfig = new Properties();
        consumerConfig.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfig.putIfAbsent(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerConfig.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumerConfig.putIfAbsent(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());
        consumerConfig.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaMessageDeserializer.class.getName());
        consumerConfig.putIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%s", eventFlowProperties.getKafka().getHostname(), eventFlowProperties.getKafka().getPort()));
        consumerConfig.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, springApplicationName);
        return consumerConfig;
    }

    @Bean("eventProducerConfig")
    public Properties eventProducerConfig(final EventFlowProperties eventFlowProperties) {
        final Properties producerConfig = new Properties();
        producerConfig.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaMessageSerializer.class);
        producerConfig.putIfAbsent(ProducerConfig.ACKS_CONFIG, "all");
        producerConfig.putIfAbsent(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        producerConfig.putIfAbsent(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        producerConfig.putIfAbsent(ProducerConfig.COMPRESSION_TYPE_CONFIG, "zstd");
        producerConfig.putIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%s", eventFlowProperties.getKafka().getHostname(), eventFlowProperties.getKafka().getPort()));

        return producerConfig;
    }


    @Bean("kafkaObjectMapper")
    public ObjectMapper kafkaObjectMapper(final ObjectMapper objectMapper) {
        final ObjectMapper kafkaObjectMapper = objectMapper.copy();
        kafkaObjectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE,
                JsonTypeInfo.As.PROPERTY);
        return kafkaObjectMapper;
    }

    @Bean
    public KafkaConsumer<String, Event> eventConsumer(@Qualifier(value = "eventConsumerConfig") final Properties eventConsumerConfig,
                                                      @Qualifier(value = "kafkaObjectMapper") final ObjectMapper kafkaObjectMapper) {
        return new KafkaConsumer<>(eventConsumerConfig, new StringDeserializer(), new KafkaMessageDeserializer<>(Event.class, kafkaObjectMapper));
    }

    @Bean
    public KafkaProducer<String, Event> eventProducer(@Qualifier(value = "eventProducerConfig") final Properties eventProducerConfig,
                                                      @Qualifier(value = "kafkaObjectMapper") final ObjectMapper kafkaObjectMapper) {
        return new KafkaProducer<>(eventProducerConfig, new StringSerializer(), new KafkaMessageSerializer<>(kafkaObjectMapper));
    }

}
