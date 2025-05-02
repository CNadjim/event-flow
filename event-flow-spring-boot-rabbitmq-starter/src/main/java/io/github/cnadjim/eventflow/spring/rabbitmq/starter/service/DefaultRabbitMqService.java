package io.github.cnadjim.eventflow.spring.rabbitmq.starter.service;

import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.consumer.RabbitMqSubscriberConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultRabbitMqService implements RabbitMqService {

    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void createOrModifyQueue(Topic topic) {
        final Queue queue = QueueBuilder.durable(topic.name())
                .ttl(topic.retentionInMsAsInt())
                .build();
        amqpAdmin.declareQueue(queue);
    }

    @Override
    public void createConsumer(MessageSubscriber messageSubscriber) {
        // Ensure the queue exists before creating the consumer
        createOrModifyQueue(messageSubscriber.topic());
        new RabbitMqSubscriberConsumer(messageSubscriber, rabbitTemplate);
    }

}
