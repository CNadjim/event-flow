package io.github.cnadjim.eventflow.spring.mongo.starter.config;

import io.github.cnadjim.eventflow.core.port.AggregateStore;
import io.github.cnadjim.eventflow.core.port.EventStore;
import io.github.cnadjim.eventflow.spring.mongo.starter.repository.MongoAggregateEntityRepository;
import io.github.cnadjim.eventflow.spring.mongo.starter.repository.MongoEventEntityRepository;
import io.github.cnadjim.eventflow.spring.mongo.starter.spi.MongoAggregateStore;
import io.github.cnadjim.eventflow.spring.mongo.starter.spi.MongoEventStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoEventFlowConfig {

    @Bean
    public EventStore eventStore(final MongoEventEntityRepository mongoEventEntityRepository,
                                 @Qualifier("eventFlowMongoTemplate") final MongoTemplate eventFlowMongoTemplate) {
        return new MongoEventStore(eventFlowMongoTemplate, mongoEventEntityRepository);
    }

    @Bean
    public AggregateStore aggregateStore(final MongoAggregateEntityRepository mongoAggregateEntityRepository) {
        return new MongoAggregateStore(mongoAggregateEntityRepository);
    }
}
