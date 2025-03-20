package io.github.cnadjim.eventflow.spring.mongo.starter.repository;

import io.github.cnadjim.eventflow.spring.mongo.starter.entity.MongoAggregateEntity;
import io.github.cnadjim.eventflow.spring.mongo.starter.entity.MongoEventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MongoAggregateEntityRepository extends MongoRepository<MongoAggregateEntity, String> {
    Optional<MongoAggregateEntity> findTopByAggregateIdOrderByVersionDesc(String aggregateId);
}
