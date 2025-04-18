package io.github.cnadjim.eventflow.spring.rabbitmq.starter.domain;

import io.github.cnadjim.eventflow.core.domain.message.Message;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
@Getter
public class DefaultEventFlowRabbitMqConsumer implements EventFlowRabbitMqConsumer {

    private final Topic topic;
    private final Consumer<Void> onShutdown;
    private final ConnectionFactory connectionFactory;
    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Collection<MessageSubscriber<?>> subscribers = new CopyOnWriteArrayList<>();
    private final SimpleMessageListenerContainer container;

    public DefaultEventFlowRabbitMqConsumer(Topic topic, Consumer<Void> onShutdown, ConnectionFactory connectionFactory, RabbitTemplate rabbitTemplate, AmqpAdmin amqpAdmin, MessageConverter messageConverter) {
        this.topic = topic;
        this.onShutdown = onShutdown;
        this.connectionFactory = connectionFactory;
        this.rabbitTemplate = rabbitTemplate;
        this.messageConverter = messageConverter;

        // Create queue for the topic
        Queue queue = new Queue(topic.name(), true);
        amqpAdmin.declareQueue(queue);

        // Set up message listener container
        this.container = new SimpleMessageListenerContainer();
        this.container.setConnectionFactory(connectionFactory);
        this.container.setQueueNames(topic.name());

        // Set up message listener
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(new Object() {
            @SuppressWarnings("unused")
            public void handleMessage(Message message) {
                sendMessageToSubscriber(message);
            }
        }, "handleMessage");

        // Set the message converter
        listenerAdapter.setMessageConverter(messageConverter);

        this.container.setMessageListener(listenerAdapter);

        // Start the container
        run();
    }

    @Override
    public void run() {
        log.debug("{} consumer started", topic.name());
        try {
            if (!container.isRunning()) {
                container.start();
            }
        } catch (Exception exception) {
            log.error("{} consumer start exception: {}", topic.name(), ExceptionUtils.getRootCauseMessage(exception), exception);
        }
    }

    @Override
    public void shutdown() {
        try {
            running.set(false);
            if (container.isRunning()) {
                container.stop();
            }
        } catch (Exception exception) {
            log.error("{} consumer shutting down exception", topic.name(), exception);
        } finally {
            onShutdown.accept(null);
            log.debug("{} consumer shutting down", topic.name());
        }
    }
}
