package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.Event;

public interface EventStore {
    void save(Event event);

    void deleteAllByAggregateId(String aggregateId);

    Iterable<Event> findAllByAggregateIdOrderByTimestampAsc(String aggregateId);

    Iterable<Event> findAllByAggregateIdOrderByTimestampAscStartFrom(String aggregateId, int startFrom);
}
