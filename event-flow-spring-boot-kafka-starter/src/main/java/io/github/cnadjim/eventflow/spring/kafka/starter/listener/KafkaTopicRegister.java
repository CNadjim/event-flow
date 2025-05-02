package io.github.cnadjim.eventflow.spring.kafka.starter.listener;

import io.github.cnadjim.eventflow.core.service.TopicService;
import io.github.cnadjim.eventflow.spring.kafka.starter.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTopicRegister {
    private final TopicService topicService;
    private final KafkaService kafkaService;

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent ignored) {
        topicService.findAll().forEach(kafkaService::createOrModifyTopic);
    }
}
