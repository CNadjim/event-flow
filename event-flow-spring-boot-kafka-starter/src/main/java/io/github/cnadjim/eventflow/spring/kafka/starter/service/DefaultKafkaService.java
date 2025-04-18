package io.github.cnadjim.eventflow.spring.kafka.starter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;
import io.github.cnadjim.eventflow.core.domain.topic.MessageTopic;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.spring.kafka.starter.domain.DefaultEventFlowKafkaConsumer;
import io.github.cnadjim.eventflow.spring.kafka.starter.domain.EventFlowKafkaConsumer;
import io.github.cnadjim.eventflow.spring.kafka.starter.kafka.KafkaMessageDeserializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
public class DefaultKafkaService implements KafkaService {

    private final KafkaAdmin kafkaAdmin;
    private final ObjectMapper kafkaObjectMapper;
    private final Properties messageConsumerConfig;
    private final Properties messageResultConsumerConfig;
    private final ConcurrentMap<String, EventFlowKafkaConsumer> consumers = new ConcurrentHashMap<>();


    @Override
    public void createOrModifyTopic(Topic topic) {
        NewTopic newTopicConfig = new NewTopic(topic.name(), 1, (short) 1);
        newTopicConfig.configs(Map.of(
            TopicConfig.RETENTION_MS_CONFIG, String.valueOf(topic.retentionInMs()),
            TopicConfig.DELETE_RETENTION_MS_CONFIG, String.valueOf(topic.retentionInMs())
        ));
        kafkaAdmin.createOrModifyTopics(newTopicConfig);
    }

    @Override
    public EventFlowKafkaConsumer getConsumerByTopic(Topic topic) {
        return consumers.computeIfAbsent(topic.name(), newTopic -> new DefaultEventFlowKafkaConsumer(topic, onShutdown -> consumers.remove(topic.name()), createConsumer(topic)));
    }

    private KafkaConsumer<String, Message> createConsumer(Topic topic) {
        if (topic instanceof MessageTopic) {
            return new KafkaConsumer<>(messageConsumerConfig, new StringDeserializer(), new KafkaMessageDeserializer(kafkaObjectMapper));
        } else if (topic instanceof MessageResultTopic) {
            return new KafkaConsumer<>(messageResultConsumerConfig, new StringDeserializer(), new KafkaMessageDeserializer(kafkaObjectMapper));
        } else {
            throw new IllegalStateException("Unexpected value: " + topic);
        }
    }
}
