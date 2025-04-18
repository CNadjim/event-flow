package io.github.cnadjim.eventflow.spring.rabbitmq.starter.service;

import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;
import io.github.cnadjim.eventflow.core.domain.topic.MessageTopic;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.domain.DefaultEventFlowRabbitMqConsumer;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.domain.EventFlowRabbitMqConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor
public class DefaultRabbitMqService implements RabbitMqService {

    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentMap<String, EventFlowRabbitMqConsumer> consumers = new ConcurrentHashMap<>();

    @Override
    public void createOrModifyQueue(Topic topic) {
        final Queue queue = QueueBuilder.durable(topic.name()).build();
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
