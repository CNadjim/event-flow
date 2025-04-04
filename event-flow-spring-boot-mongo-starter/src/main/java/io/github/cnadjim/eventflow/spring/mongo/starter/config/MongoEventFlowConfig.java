package io.github.cnadjim.eventflow.spring.mongo.starter.config;

import io.github.cnadjim.eventflow.core.spi.AggregateStore;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.spring.mongo.starter.repository.MongoAggregateEntityRepository;
import io.github.cnadjim.eventflow.spring.mongo.starter.repository.MongoEventEntityRepository;
import io.github.cnadjim.eventflow.spring.mongo.starter.spi.MongoAggregateStore;
import io.github.cnadjim.eventflow.spring.mongo.starter.spi.MongoEventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoEventFlowConfig {

    @Bean
    public EventStore eventStore(final MongoEventEntityRepository mongoEventEntityRepository) {
        return new MongoEventStore(mongoEventEntityRepository);
    }

    @Bean
    public AggregateStore aggregateStore(final MongoAggregateEntityRepository mongoAggregateEntityRepository) {
        return new MongoAggregateStore(mongoAggregateEntityRepository);
    }
}
