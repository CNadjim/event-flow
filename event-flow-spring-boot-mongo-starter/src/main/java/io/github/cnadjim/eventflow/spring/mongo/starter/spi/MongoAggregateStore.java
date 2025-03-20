package io.github.cnadjim.eventflow.spring.mongo.starter.spi;

import io.github.cnadjim.eventflow.core.domain.AggregateWrapper;
import io.github.cnadjim.eventflow.core.spi.AggregateStore;
import io.github.cnadjim.eventflow.spring.mongo.starter.entity.MongoAggregateEntity;
import io.github.cnadjim.eventflow.spring.mongo.starter.repository.MongoAggregateEntityRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class MongoAggregateStore implements AggregateStore {

    private final MongoAggregateEntityRepository mongoAggregateEntityRepository;

    public MongoAggregateStore(MongoAggregateEntityRepository mongoAggregateEntityRepository) {
        this.mongoAggregateEntityRepository = mongoAggregateEntityRepository;
    }

    @Override
    @Transactional
    public void save(AggregateWrapper aggregateWrapper) {
        Optional.ofNullable(aggregateWrapper)
                .map(MongoAggregateEntity::fromAggregate)
                .ifPresent(mongoAggregateEntityRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AggregateWrapper> findTopByAggregateIdOrderByVersionDesc(String aggregateId) {
        return mongoAggregateEntityRepository
                .findTopByAggregateIdOrderByVersionDesc(aggregateId)
                .map(MongoAggregateEntity::toAggregate);
    }
}
