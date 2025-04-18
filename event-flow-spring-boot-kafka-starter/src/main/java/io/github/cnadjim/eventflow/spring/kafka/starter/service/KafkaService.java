package io.github.cnadjim.eventflow.spring.kafka.starter.service;

import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.spring.kafka.starter.domain.EventFlowKafkaConsumer;

public interface KafkaService {

     void createOrModifyTopic(Topic topic);

     EventFlowKafkaConsumer getConsumerByTopic(Topic topic);
}
