package io.github.cnadjim.eventflow.spring.rabbitmq.starter.service;

import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.consumer.RabbitMqSubscriberConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
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
        // Ensure the exchange exists before creating the consumer
        createOrModifyQueue(messageSubscriber.topic());

        // Create a unique queue name for this subscriber
        String queueName = messageSubscriber.topic().name() + "." + applicationName;

        // Create a non-durable, exclusive queue with the generated name
        Queue queue = QueueBuilder.nonDurable(queueName)
                .ttl(messageSubscriber.topic().retentionInMsAsInt())
                .build();

        amqpAdmin.declareQueue(queue);

        // Bind the queue to the exchange
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(new FanoutExchange(messageSubscriber.topic().name(), true, false)));

        // Create the consumer with the unique queue name
        new RabbitMqSubscriberConsumer(messageSubscriber, rabbitTemplate, queueName);
    }

}
