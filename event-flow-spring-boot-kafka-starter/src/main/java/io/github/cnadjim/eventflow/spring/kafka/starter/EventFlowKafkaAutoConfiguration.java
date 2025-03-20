package io.github.cnadjim.eventflow.spring.kafka.starter;

import io.github.cnadjim.eventflow.spring.kafka.starter.config.EventFlowConfig;
import io.github.cnadjim.eventflow.spring.kafka.starter.config.KafkaConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        EventFlowConfig.class,
        KafkaConfig.class,
})
public class EventFlowKafkaAutoConfiguration {

}
