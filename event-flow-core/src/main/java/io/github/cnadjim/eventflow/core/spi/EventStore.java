package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.EventWrapper;

import java.util.List;

public interface EventStore {
    void save(EventWrapper event);
    void saveAll(List<EventWrapper> events);
    Iterable<EventWrapper> findAllByAggregateIdOrderByTimestampAsc(String aggregateId);
    Iterable<EventWrapper> findAllByAggregateIdOrderByTimestampAscStartFrom(String aggregateId, int startFrom);
}
