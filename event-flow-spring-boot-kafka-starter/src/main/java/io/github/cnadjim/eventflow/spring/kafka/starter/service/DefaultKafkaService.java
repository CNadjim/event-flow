package io.github.cnadjim.eventflow.spring.kafka.starter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.spring.kafka.starter.consumer.KafkaSubscriberConsumer;
import io.github.cnadjim.eventflow.spring.kafka.starter.kafka.KafkaMessageDeserializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;

@Service
public class DefaultKafkaService implements KafkaService {

    private final KafkaAdmin kafkaAdmin;
    private final ObjectMapper kafkaObjectMapper;
    private final Properties messageConsumerProperties;

    public DefaultKafkaService(KafkaAdmin kafkaAdmin,
                               @Qualifier("kafkaObjectMapper") ObjectMapper kafkaObjectMapper,
                               @Qualifier("messageConsumerProperties") Properties messageConsumerProperties) {
        this.kafkaAdmin = kafkaAdmin;
        this.kafkaObjectMapper = kafkaObjectMapper;
        this.messageConsumerProperties = messageConsumerProperties;
    }

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
    public void createConsumer(MessageSubscriber messageSubscriber) {
        createOrModifyTopic(messageSubscriber.topic());
        new KafkaSubscriberConsumer(messageSubscriber, messageConsumerProperties, new KafkaMessageDeserializer(kafkaObjectMapper));
    }

}
