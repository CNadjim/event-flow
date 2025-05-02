package io.github.cnadjim.eventflow.spring.kafka.starter.service;

import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

public interface KafkaService {

     void createOrModifyTopic(Topic topic);

     void createConsumer(MessageSubscriber messageSubscriber);


}
