package io.github.cnadjim.eventflow.spring.kafka.starter.service;

import io.github.cnadjim.eventflow.core.domain.Topic;
import io.github.cnadjim.eventflow.spring.kafka.starter.domain.EventFlowKafkaConsumer;

public interface KafkaService {
     EventFlowKafkaConsumer getConsumerByTopic(Topic topic);
}
