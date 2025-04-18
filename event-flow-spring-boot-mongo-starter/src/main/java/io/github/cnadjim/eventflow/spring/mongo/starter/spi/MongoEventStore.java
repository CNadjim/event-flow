package io.github.cnadjim.eventflow.spring.mongo.starter.spi;

import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.spring.mongo.starter.entity.MongoEventEntity;
import io.github.cnadjim.eventflow.spring.mongo.starter.repository.MongoEventEntityRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class MongoEventStore implements EventStore {

    private final MongoEventEntityRepository eventEntityRepository;

    public MongoEventStore(MongoEventEntityRepository eventEntityRepository) {
        this.eventEntityRepository = eventEntityRepository;
    }

    @Override
    @Transactional
    public void save(Event event) {
        Optional.ofNullable(event)
                .map(MongoEventEntity::fromEvent)
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
                .map(MongoEventEntity::toEvent)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findAllByAggregateIdOrderByTimestampAscStartFrom(String aggregateId, int startFrom) {
        return eventEntityRepository.findAllByAggregateIdOrderByTimestampAsc(aggregateId, PageRequest.of(1, startFrom))
                .stream()
                .map(MongoEventEntity::toEvent)
                .toList();
    }
}
