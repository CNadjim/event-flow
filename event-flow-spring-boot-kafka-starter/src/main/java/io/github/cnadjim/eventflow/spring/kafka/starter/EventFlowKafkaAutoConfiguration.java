package io.github.cnadjim.eventflow.spring.kafka.starter;

import io.github.cnadjim.eventflow.spring.kafka.starter.config.EventFlowConfig;
import io.github.cnadjim.eventflow.spring.kafka.starter.config.KafkaConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        EventFlowConfig.class,
        KafkaConfig.class,
})
@ConditionalOnProperty(prefix = "event-flow.kafka", name = "hostname")
public class EventFlowKafkaAutoConfiguration {

}
