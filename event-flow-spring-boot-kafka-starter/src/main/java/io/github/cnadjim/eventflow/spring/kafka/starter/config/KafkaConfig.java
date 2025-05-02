package io.github.cnadjim.eventflow.spring.kafka.starter.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.spring.kafka.starter.kafka.KafkaMessageDeserializer;
import io.github.cnadjim.eventflow.spring.kafka.starter.kafka.KafkaMessageSerializer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
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
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class KafkaConfig {

    @Value("${spring.application.name}")
    private String springApplicationName;

    @Value("${spring.kafka.bootstrap-servers}")
    private String boostrapServers;

    @Value("${server.port}")
    private String port;

    @Bean("messageConsumerProperties")
    public Properties messageConsumerProperties() {
        final Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaMessageDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerConfig.putIfAbsent(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        consumerConfig.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, springApplicationName);
        return consumerConfig;
    }

    @Bean("messageProducerProperties")
    public Properties messageProducerProperties() {
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

    @Bean("kafkaAdminConfig")
    public Properties kafkaAdminConfig() {
        final Properties adminConfig = new Properties();
        adminConfig.putIfAbsent(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        return adminConfig;
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
    public KafkaProducer<String, Message> messageProducer(@Qualifier(value = "messageProducerProperties") final Properties messageProducerProperties,
                                                          @Qualifier(value = "kafkaObjectMapper") final ObjectMapper kafkaObjectMapper) {
        return new KafkaProducer<>(messageProducerProperties, new StringSerializer(), new KafkaMessageSerializer(kafkaObjectMapper));
    }

    @Bean
    public AdminClient adminClient(@Qualifier(value = "kafkaAdminConfig") final Properties kafkaAdminConfig) {
        return AdminClient.create(kafkaAdminConfig);
    }

    @Bean
    public KafkaAdmin kafkaAdmin(@Qualifier(value = "kafkaAdminConfig") final Properties kafkaAdminConfig) {
        final Map<String, Object> configs = new HashMap<>();
        for (String key : kafkaAdminConfig.stringPropertyNames()) {
            configs.put(key, kafkaAdminConfig.getProperty(key));
        }
        return new KafkaAdmin(configs);
    }
}
