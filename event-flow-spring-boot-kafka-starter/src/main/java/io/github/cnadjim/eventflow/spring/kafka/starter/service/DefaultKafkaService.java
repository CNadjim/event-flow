package io.github.cnadjim.eventflow.spring.kafka.starter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cnadjim.eventflow.core.domain.Message;
import io.github.cnadjim.eventflow.core.domain.Topic;
import io.github.cnadjim.eventflow.spring.kafka.starter.domain.DefaultEventFlowKafkaConsumer;
import io.github.cnadjim.eventflow.spring.kafka.starter.domain.EventFlowKafkaConsumer;
import io.github.cnadjim.eventflow.spring.kafka.starter.kafka.KafkaMessageDeserializer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class DefaultKafkaService implements KafkaService {

    private final ObjectMapper kafkaObjectMapper;
    private final Properties messageConsumerConfig;
    private final Properties messageResultConsumerConfig;
    private final ConcurrentMap<String, EventFlowKafkaConsumer> consumers = new ConcurrentHashMap<>();

    public DefaultKafkaService(@Qualifier("kafkaObjectMapper") ObjectMapper kafkaObjectMapper,
                               @Qualifier("messageConsumerConfig") Properties messageConsumerConfig,
                               @Qualifier("messageResultConsumerConfig") Properties messageResultConsumerConfig) {
        this.kafkaObjectMapper = kafkaObjectMapper;
        this.messageConsumerConfig = messageConsumerConfig;
        this.messageResultConsumerConfig = messageResultConsumerConfig;
    }

    @Override
    public EventFlowKafkaConsumer getConsumerByTopic(Topic topic) {
        return consumers.computeIfAbsent(topic.name(), newTopic -> new DefaultEventFlowKafkaConsumer(topic, onShutdown -> consumers.remove(topic.name()), createConsumer(topic)));
    }

    private KafkaConsumer<String, Message> createConsumer(Topic topic) {
        switch (topic.type()) {
            case MESSAGE -> {
                return new KafkaConsumer<>(messageConsumerConfig, new StringDeserializer(), new KafkaMessageDeserializer(kafkaObjectMapper));
            }
            case MESSAGE_RESULT -> {
                return new KafkaConsumer<>(messageResultConsumerConfig, new StringDeserializer(), new KafkaMessageDeserializer(kafkaObjectMapper));
            }
            default -> throw new IllegalStateException("Unexpected value: " + topic.type());
        }
    }
}
