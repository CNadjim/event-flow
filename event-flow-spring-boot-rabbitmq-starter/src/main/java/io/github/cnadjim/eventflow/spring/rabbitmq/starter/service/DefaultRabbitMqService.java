package io.github.cnadjim.eventflow.spring.rabbitmq.starter.service;

import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.consumer.RabbitMqSubscriberConsumer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultRabbitMqService implements RabbitMqService {

    @Value("${spring.application.name}")
    private String applicationName;

    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void createOrModifyQueue(Topic topic) {
        // Declare a fanout exchange for the topic
        final FanoutExchange exchange = new FanoutExchange(topic.name(), true, false);
        amqpAdmin.declareExchange(exchange);
    }

    @Override
    public void createConsumer(MessageSubscriber messageSubscriber) {
        final Topic topic = messageSubscriber.topic();

        // Ensure the exchange exists before creating the consumer
        createOrModifyQueue(topic);

        // Create a unique queue name for this subscriber
        final String queueName;
        final String handlerName = messageSubscriber.handlerName();

        if (StringUtils.isBlank(handlerName)) {
            queueName = String.format("[ %s ] => [ %s ]", topic.name(), applicationName);
        } else {
            queueName = String.format("[ %s ] => [ %s ] => [ %s ]", topic.name(), applicationName, messageSubscriber.handlerName());
        }

        // Create a non-durable, exclusive queue with the generated name
        Queue queue = QueueBuilder.nonDurable(queueName)
                .ttl(topic.retentionInMsAsInt())
                .build();

        amqpAdmin.declareQueue(queue);

        // Bind the queue to the exchange
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(new FanoutExchange(topic.name(), true, false)));

        // Create the consumer with the unique queue name
        new RabbitMqSubscriberConsumer(messageSubscriber, rabbitTemplate, queueName);
    }

}
