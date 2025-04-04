package io.github.cnadjim.eventflow.spring.kafka.starter.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import io.github.cnadjim.eventflow.core.domain.Message;
import io.github.cnadjim.eventflow.spring.kafka.starter.kafka.KafkaMessageDeserializer;
import io.github.cnadjim.eventflow.spring.kafka.starter.kafka.KafkaMessageSerializer;
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
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfig {

    @Value("${spring.application.name}")
    private String springApplicationName;

    @Value("${spring.kafka.bootstrap-servers}")
    private String boostrapServers;

    @Value("${server.port}")
    private String port;

    @Bean("messageConsumerConfig")
    public Properties messageConsumerConfig() {
        final Properties consumerConfig = new Properties();
        consumerConfig.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaMessageDeserializer.class.getName());
        consumerConfig.putIfAbsent(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerConfig.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumerConfig.putIfAbsent(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());
        consumerConfig.putIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        consumerConfig.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, springApplicationName);
        return consumerConfig;
    }


    @Bean("messageResultConsumerConfig")
    public Properties messageResultConsumerConfig() {
        final Properties consumerConfig = new Properties();
        consumerConfig.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaMessageDeserializer.class.getName());
        consumerConfig.putIfAbsent(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerConfig.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerConfig.putIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        consumerConfig.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, springApplicationName);
        return consumerConfig;
    }

    @Bean("messageProducerConfig")
    public Properties messageProducerConfig() {
        final Properties producerConfig = new Properties();
        producerConfig.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaMessageSerializer.class);
        producerConfig.putIfAbsent(ProducerConfig.ACKS_CONFIG, "all");
        producerConfig.putIfAbsent(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        producerConfig.putIfAbsent(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        producerConfig.putIfAbsent(ProducerConfig.COMPRESSION_TYPE_CONFIG, "zstd");
        producerConfig.putIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        return producerConfig;
    }


    @Bean("kafkaObjectMapper")
    public ObjectMapper kafkaObjectMapper(final ObjectMapper objectMapper) {
        final ObjectMapper kafkaObjectMapper = objectMapper.copy();
        kafkaObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE,
                JsonTypeInfo.As.PROPERTY
        );
        return kafkaObjectMapper;
    }

    @Bean
    public KafkaConsumer<String, Message> messageConsumer(@Qualifier(value = "kafkaObjectMapper") final ObjectMapper kafkaObjectMapper,
                                                          @Qualifier(value = "messageConsumerConfig") final Properties messageConsumerConfig) {
        return new KafkaConsumer<>(messageConsumerConfig, new StringDeserializer(), new KafkaMessageDeserializer(kafkaObjectMapper));
    }

    @Bean
    public KafkaProducer<String, Message> messageProducer(@Qualifier(value = "messageProducerConfig") final Properties messageProducerConfig,
                                                          @Qualifier(value = "kafkaObjectMapper") final ObjectMapper kafkaObjectMapper) {
        return new KafkaProducer<>(messageProducerConfig, new StringSerializer(), new KafkaMessageSerializer(kafkaObjectMapper));
    }

}
