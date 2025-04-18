package io.github.cnadjim.eventflow.spring.kafka.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;


@Profile("kafka")
@AutoConfiguration
@ComponentScan(basePackageClasses = EventFlowKafkaAutoConfiguration.class)
public class EventFlowKafkaAutoConfiguration {

}
