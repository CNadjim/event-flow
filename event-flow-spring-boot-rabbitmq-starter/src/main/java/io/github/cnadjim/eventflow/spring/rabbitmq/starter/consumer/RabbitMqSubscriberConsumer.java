package io.github.cnadjim.eventflow.spring.rabbitmq.starter.consumer;

import com.rabbitmq.client.Channel;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class RabbitMqSubscriberConsumer implements Runnable, ChannelAwareMessageListener {

    private final MessageSubscriber subscriber;
    private final RabbitTemplate rabbitTemplate;
    private final SimpleMessageListenerContainer simpleMessageListenerContainer;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AtomicBoolean shutdownInitiated = new AtomicBoolean(false);
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    public RabbitMqSubscriberConsumer(MessageSubscriber subscriber, RabbitTemplate rabbitTemplate, String queueName) {
        this.subscriber = subscriber;
        this.rabbitTemplate = rabbitTemplate;
        this.simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(rabbitTemplate.getConnectionFactory());
        simpleMessageListenerContainer.setQueueNames(queueName);
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        simpleMessageListenerContainer.setMessageListener(this);
        // Configure shutdown timeout
        simpleMessageListenerContainer.setShutdownTimeout(10000); // 10 seconds
        executorService.submit(this);
    }

    @Override
    public void run() {
        subscriber.onSubscribe(this::shutdown);
        simpleMessageListenerContainer.start();
    }

    public void shutdown() {
        if (!shutdownInitiated.compareAndSet(false, true)) {
            // Shutdown already initiated
            return;
        }

        executorService.submit(() -> {
            try {
                log.info("Starting shutdown process");
                simpleMessageListenerContainer.stop();

                boolean stopped = awaitContainerShutdown();

                if (stopped) {
                    log.info("Container stopped successfully");
                } else {
                    log.warn("Container shutdown timeout reached, forcing stop");
                }

                log.info("Shutting down executor");
                executorService.shutdown();
            } catch (Exception e) {
                log.error("Error during shutdown", e);
                executorService.shutdownNow();
            }
        });
    }

    private boolean awaitContainerShutdown() {
        try {
            return shutdownLatch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            log.warn("Shutdown interrupted");
            return false;
        }
    }

    @Override
    public void onMessage(Message message, Channel channel) {
        try {
            io.github.cnadjim.eventflow.core.domain.message.Message convertedMessage =
                    (io.github.cnadjim.eventflow.core.domain.message.Message)
                            rabbitTemplate.getMessageConverter().fromMessage(message);

            boolean ack = subscriber.onNext(convertedMessage);
            if (ack) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        } catch (Exception exception) {
            log.error("Error processing message", exception);
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception ackException) {
                log.error("Failed to reject message", ackException);
            }
        }
    }
}
