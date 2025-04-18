package io.github.cnadjim.eventflow.spring.rabbitmq.starter.spi;

import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.spi.MessageBus;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.domain.EventFlowRabbitMqConsumer;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.service.RabbitMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
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
        rabbitTemplate.convertAndSend(topicName, message);
    }

    @Override
    public <MESSAGE extends Message> void subscribe(MessageSubscriber<MESSAGE> messageSubscriber) {
        final Topic topic = messageSubscriber.topic();
        rabbitMqService.createOrModifyQueue(topic);
        final EventFlowRabbitMqConsumer eventFlowRabbitMqConsumer = rabbitMqService.getConsumerByTopic(topic);
        eventFlowRabbitMqConsumer.addSubscriber(messageSubscriber);
    }
}
