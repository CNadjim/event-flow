package io.github.cnadjim.eventflow.spring.rabbitmq.starter.spi;

import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.service.RabbitMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitMqMessageBus implements MessageBus {

    private final RabbitMqService rabbitMqService;
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqMessageBus(final RabbitMqService rabbitMqService,
                             final RabbitTemplate rabbitTemplate) {
        this.rabbitMqService = rabbitMqService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(Message message) {
        final String topicName = message.topic().name();
        // Send to the exchange with the topic name, with an empty routing key since it's a fanout exchange
        rabbitTemplate.convertAndSend(topicName, "", message);
    }

    @Override
    public void subscribe(MessageSubscriber subscriber) {
        rabbitMqService.createConsumer(subscriber);
    }
}
