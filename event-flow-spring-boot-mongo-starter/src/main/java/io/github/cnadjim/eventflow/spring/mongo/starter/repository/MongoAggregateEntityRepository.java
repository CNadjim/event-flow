package io.github.cnadjim.eventflow.spring.mongo.starter.repository;

import io.github.cnadjim.eventflow.spring.mongo.starter.entity.MongoAggregateEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoAggregateEntityRepository extends MongoRepository<MongoAggregateEntity, String> {
    void deleteAllByAggregateId(String aggregateId);
    Optional<MongoAggregateEntity> findTopByAggregateIdOrderByVersionDesc(String aggregateId);
}
