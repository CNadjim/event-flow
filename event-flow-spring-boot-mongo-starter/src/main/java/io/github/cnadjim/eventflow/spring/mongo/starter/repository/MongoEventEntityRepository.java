package io.github.cnadjim.eventflow.spring.mongo.starter.repository;

import io.github.cnadjim.eventflow.spring.mongo.starter.entity.MongoEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoEventEntityRepository extends MongoRepository<MongoEventEntity, String> {
    List<MongoEventEntity> findAllByAggregateIdOrderByTimestampAsc(String aggregateId);
    List<MongoEventEntity> findAllByAggregateIdOrderByTimestampAsc(String aggregateId, Pageable pageable);
}
