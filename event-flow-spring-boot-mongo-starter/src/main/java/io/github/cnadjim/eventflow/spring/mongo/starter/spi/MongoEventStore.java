package io.github.cnadjim.eventflow.spring.mongo.starter.spi;

import io.github.cnadjim.eventflow.core.domain.EventWrapper;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.spring.mongo.starter.entity.MongoEventEntity;
import io.github.cnadjim.eventflow.spring.mongo.starter.repository.MongoEventEntityRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Objects.nonNull;

public class MongoEventStore implements EventStore {

    private final MongoEventEntityRepository eventEntityRepository;

    public MongoEventStore(MongoEventEntityRepository eventEntityRepository) {
        this.eventEntityRepository = eventEntityRepository;
    }

    @Override
    @Transactional
    public void save(EventWrapper event) {
        Optional.ofNullable(event)
                .map(MongoEventEntity::fromEvent)
                .ifPresent(eventEntityRepository::save);
    }

    @Override
    @Transactional
    public void saveAll(List<EventWrapper> events) {
        final Collection<MongoEventEntity> eventEntities = Optional.ofNullable(events)
                .orElse(Collections.emptyList())
                .stream()
                .map(MongoEventEntity::fromEvent)
                .toList();
        eventEntityRepository.saveAll(eventEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventWrapper> findAllByAggregateIdOrderByTimestampAsc(String aggregateId) {
        return eventEntityRepository.findAllByAggregateIdOrderByTimestampAsc(aggregateId)
                .stream()
                .map(MongoEventEntity::toEvent)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventWrapper> findAllByAggregateIdOrderByTimestampAscStartFrom(String aggregateId, int startFrom) {
        return eventEntityRepository.findAllByAggregateIdOrderByTimestampAsc(aggregateId, PageRequest.of(1, startFrom))
                .stream()
                .map(MongoEventEntity::toEvent)
                .toList();
    }
}
