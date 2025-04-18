package io.github.cnadjim.eventflow.spring.rabbitmq.starter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.domain.DefaultEventFlowRabbitMqConsumer;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.domain.EventFlowRabbitMqConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
public class DefaultRabbitMqService implements RabbitMqService {

    private final AmqpAdmin amqpAdmin;
    private final ObjectMapper rabbitMqObjectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentMap<String, EventFlowRabbitMqConsumer> consumers = new ConcurrentHashMap<>();

    @Override
    public void createOrModifyQueue(Topic topic) {
        Map<String, Object> arguments = new HashMap<>();
        // Set message TTL based on topic retention
        if (topic.retentionInMs() > 0) {
            arguments.put("x-message-ttl", topic.retentionInMs());
        }

        Queue queue = QueueBuilder.durable(topic.name())
                .withArguments(arguments)
                .build();

        amqpAdmin.declareQueue(queue);
    }

    @Override
    public EventFlowRabbitMqConsumer getConsumerByTopic(Topic topic) {
        return consumers.computeIfAbsent(topic.name(), 
            newTopic -> new DefaultEventFlowRabbitMqConsumer(
                topic, 
                onShutdown -> consumers.remove(topic.name()), 
                rabbitTemplate.getConnectionFactory(),
                rabbitTemplate,
                amqpAdmin,
                rabbitTemplate.getMessageConverter()
            )
        );
    }
}
