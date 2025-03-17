package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.Event;

import java.util.List;

public interface EventStore {
    void save(Event event);
    void saveAll(List<Event> events);
    List<Event> findAllByAggregateIdOrderByTimestampAsc(String aggregateId);
}
