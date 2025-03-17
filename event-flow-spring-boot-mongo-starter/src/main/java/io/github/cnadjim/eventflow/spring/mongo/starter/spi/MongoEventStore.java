package io.github.cnadjim.eventflow.spring.mongo.starter.spi;

import io.github.cnadjim.eventflow.core.domain.Event;
import io.github.cnadjim.eventflow.core.spi.EventStore;
import io.github.cnadjim.eventflow.spring.mongo.starter.entity.MongoEventEntity;
import io.github.cnadjim.eventflow.spring.mongo.starter.repository.MongoEventEntityRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

public class MongoEventStore implements EventStore {

    private final MongoEventEntityRepository eventEntityRepository;

    public MongoEventStore(MongoEventEntityRepository eventEntityRepository) {
        this.eventEntityRepository = eventEntityRepository;
    }

    @Override
    @Transactional
    public void save(Event event) {
        if (nonNull(event)) {
            eventEntityRepository.save(MongoEventEntity.fromEvent(event));
        }
    }

    @Override
    @Transactional
    public void saveAll(List<Event> events) {
        final Collection<MongoEventEntity> eventEntities = Optional.ofNullable(events)
                .orElse(Collections.emptyList())
                .stream()
                .map(MongoEventEntity::fromEvent)
                .toList();
        eventEntityRepository.saveAll(eventEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findAllByAggregateIdOrderByTimestampAsc(String aggregateId) {
        return eventEntityRepository.findAllByAggregateIdOrderByTimestampAsc(aggregateId)
                .stream()
                .map(MongoEventEntity::toEvent)
                .toList();
    }
}
