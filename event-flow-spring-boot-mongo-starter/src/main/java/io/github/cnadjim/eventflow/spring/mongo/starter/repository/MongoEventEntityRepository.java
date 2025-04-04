package io.github.cnadjim.eventflow.spring.mongo.starter.repository;

import io.github.cnadjim.eventflow.spring.mongo.starter.entity.MongoEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MongoEventEntityRepository extends MongoRepository<MongoEventEntity, String> {

    void deleteAllByAggregateId(String aggregateId);

    List<MongoEventEntity> findAllByAggregateIdOrderByTimestampAsc(String aggregateId);

    List<MongoEventEntity> findAllByAggregateIdOrderByTimestampAsc(String aggregateId, Pageable pageable);
}
