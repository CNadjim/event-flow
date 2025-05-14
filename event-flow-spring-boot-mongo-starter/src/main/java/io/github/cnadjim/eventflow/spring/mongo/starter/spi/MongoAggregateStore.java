package io.github.cnadjim.eventflow.spring.mongo.starter.spi;

import io.github.cnadjim.eventflow.core.domain.aggregate.Aggregate;
import io.github.cnadjim.eventflow.core.port.AggregateStore;
import io.github.cnadjim.eventflow.spring.mongo.starter.mapper.AggregateMapper;
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
    public void save(Aggregate aggregateWrapper) {
        Optional.ofNullable(aggregateWrapper)
                .map(AggregateMapper::toEntity)
                .ifPresent(mongoAggregateEntityRepository::save);
    }

    @Override
    public void deleteAllByAggregateId(String aggregateId) {
        mongoAggregateEntityRepository.deleteAllByAggregateId(aggregateId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Aggregate> findTopByAggregateIdOrderByVersionDesc(String aggregateId) {
        return mongoAggregateEntityRepository
                .findTopByAggregateIdOrderByVersionDesc(aggregateId)
                .map(AggregateMapper::toDomain);
    }
}
