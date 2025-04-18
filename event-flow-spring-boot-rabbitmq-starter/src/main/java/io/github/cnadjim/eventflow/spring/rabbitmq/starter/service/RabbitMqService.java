package io.github.cnadjim.eventflow.spring.rabbitmq.starter.service;

import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.domain.EventFlowRabbitMqConsumer;

public interface RabbitMqService {

    void createOrModifyQueue(Topic topic);

    EventFlowRabbitMqConsumer getConsumerByTopic(Topic topic);
}
