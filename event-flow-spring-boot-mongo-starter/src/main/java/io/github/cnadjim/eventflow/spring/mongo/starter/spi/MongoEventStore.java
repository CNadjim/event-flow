package io.github.cnadjim.eventflow.spring.mongo.starter.spi;

import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.port.EventStore;
import io.github.cnadjim.eventflow.spring.mongo.starter.entity.MongoEventEntity;
import io.github.cnadjim.eventflow.spring.mongo.starter.mapper.EventMapper;
import io.github.cnadjim.eventflow.spring.mongo.starter.repository.MongoEventEntityRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class MongoEventStore implements EventStore {

    private final MongoTemplate mongoTemplate;
    private final MongoEventEntityRepository eventEntityRepository;

    public MongoEventStore(MongoTemplate mongoTemplate,
                           MongoEventEntityRepository eventEntityRepository) {
        this.mongoTemplate = mongoTemplate;
        this.eventEntityRepository = eventEntityRepository;
    }

    @Override
    @Transactional
    public void save(Event event) {
        Optional.ofNullable(event)
                .map(EventMapper::toEntity)
                .ifPresent(eventEntityRepository::save);
    }

    @Override
    public void deleteAllByAggregateId(String aggregateId) {
        eventEntityRepository.deleteAllByAggregateId(aggregateId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findAllByAggregateIdOrderByTimestampAsc(String aggregateId) {
        return eventEntityRepository.findAllByAggregateIdOrderByTimestampAsc(aggregateId)
                .stream()
                .map(EventMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findAllByAggregateIdOrderByTimestampAscStartFrom(String aggregateId, int startFrom) {
        final Query query = new Query(Criteria.where("aggregateId").is(aggregateId))
                .with(Sort.by(Sort.Direction.ASC, "timestamp"))
                .skip(startFrom);

        final List<MongoEventEntity> events = mongoTemplate.find(query, MongoEventEntity.class);

        return events.stream()
                .map(EventMapper::toDomain)
                .toList();
    }
}
