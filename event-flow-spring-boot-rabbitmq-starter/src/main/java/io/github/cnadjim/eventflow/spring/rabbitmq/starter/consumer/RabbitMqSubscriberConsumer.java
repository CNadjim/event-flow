package io.github.cnadjim.eventflow.spring.rabbitmq.starter.consumer;

import com.rabbitmq.client.Channel;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import io.github.cnadjim.eventflow.core.domain.flux.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class RabbitMqSubscriberConsumer implements Runnable, ChannelAwareMessageListener {

    private final MessageSubscriber subscriber;
    private final RabbitTemplate rabbitTemplate;
    private final SimpleMessageListenerContainer simpleMessageListenerContainer;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public RabbitMqSubscriberConsumer(MessageSubscriber subscriber, RabbitTemplate rabbitTemplate) {
        this.subscriber = subscriber;
        this.rabbitTemplate = rabbitTemplate;
        this.simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(rabbitTemplate.getConnectionFactory());
        simpleMessageListenerContainer.setQueueNames(subscriber.topic().name());
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        simpleMessageListenerContainer.setMessageListener(this);
        executorService.submit(this);
    }

    @Override
    public void run() {
        simpleMessageListenerContainer.start();
        final Subscription subscription = this::shutdown;
        subscriber.onSubscribe(subscription);
    }

    public void shutdown() {
        simpleMessageListenerContainer.stop();
        executorService.shutdown();
    }


    @Override
    public void onMessage(Message message, Channel channel) {
        try {
            io.github.cnadjim.eventflow.core.domain.message.Message convertedMessage = (io.github.cnadjim.eventflow.core.domain.message.Message) rabbitTemplate.getMessageConverter().fromMessage(message);
            boolean ack = subscriber.onNext(convertedMessage);
            if (ack) {
                // Acknowledge the message if it was successfully processed
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                // Reject the message if it was not successfully processed
                // The third parameter (requeue) is set to true to requeue the message
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        } catch (Exception e) {
            try {
                // In case of an exception, reject the message
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception exception) {
                // Log the exception if we can't even reject the message
                log.error("Failed to reject message", exception);
            }
        }
    }
}
