package io.github.cnadjim.eventflow.spring.rabbitmq.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;


@Profile("rabbitmq")
@AutoConfiguration
@ComponentScan(basePackageClasses = EventFlowRabbitMqAutoConfiguration.class)
public class EventFlowRabbitMqAutoConfiguration {

}
