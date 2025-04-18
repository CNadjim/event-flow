package io.github.cnadjim.eventflow.spring.rabbitmq.starter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.service.DefaultRabbitMqService;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.service.RabbitMqService;
import io.github.cnadjim.eventflow.spring.rabbitmq.starter.spi.RabbitMqMessageBus;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqEventFlowConfig {

    @Bean
    public RabbitMqMessageBus rabbitMqMessageBus(final RabbitMqService rabbitMqService,
                                                final RabbitTemplate rabbitTemplate) {
        return new RabbitMqMessageBus(rabbitMqService, rabbitTemplate);
    }

    @Bean
    public RabbitMqService rabbitMqService(final AmqpAdmin amqpAdmin,
                                          @Qualifier("rabbitMqObjectMapper") final ObjectMapper rabbitMqObjectMapper,
                                          final RabbitTemplate rabbitTemplate) {
        return new DefaultRabbitMqService(amqpAdmin, rabbitMqObjectMapper, rabbitTemplate);
    }
}
