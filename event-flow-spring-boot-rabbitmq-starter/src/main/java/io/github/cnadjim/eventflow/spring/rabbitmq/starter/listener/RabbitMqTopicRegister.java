package io.github.cnadjim.eventflow.spring.rabbitmq.starter.listener;

import io.github.cnadjim.eventflow.core.api.FindTopics;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.service.RabbitMqService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMqTopicRegister {

    private final FindTopics findTopics;
    private final RabbitMqService rabbitMqService;

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent ignored) {
        findTopics.findAll().forEach(rabbitMqService::createOrModifyQueue);
    }
}
