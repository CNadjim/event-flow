package io.github.cnadjim.eventflow.spring.rabbitmq.starter.service;

import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;

public interface RabbitMqService {

    void createOrModifyQueue(Topic topic);

    void createConsumer(MessageSubscriber messageSubscriber);
}
