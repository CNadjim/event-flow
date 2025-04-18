package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.annotation.Stub;
import io.github.cnadjim.eventflow.core.domain.message.Event;
import io.github.cnadjim.eventflow.core.spi.EventStore;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Stub
public class InMemoryEventStore implements EventStore {

    private final ConcurrentMap<String, CopyOnWriteArrayList<Event>> eventsMap = new ConcurrentHashMap<>();

    @Override
    public void save(Event event) {
        if (nonNull(event)) {
            eventsMap.compute(event.aggregateId(), (newAggregateId, existingList) -> {
                if (isNull(existingList)) {
                    existingList = new CopyOnWriteArrayList<>();
                }
                existingList.add(event);
                return existingList;
            });
        }
    }

    @Override
    public void deleteAllByAggregateId(String aggregateId) {
        eventsMap.remove(aggregateId);
    }

    @Override
    public List<Event> findAllByAggregateIdOrderByTimestampAsc(String aggregateId) {
        return eventsMap.getOrDefault(aggregateId, new CopyOnWriteArrayList<>())
                .stream()
                .sorted(Comparator.comparing(Event::timestamp))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findAllByAggregateIdOrderByTimestampAscStartFrom(String aggregateId, int startFrom) {
        return eventsMap.getOrDefault(aggregateId, new CopyOnWriteArrayList<>())
                .stream()
                .sorted(Comparator.comparing(Event::timestamp))
                .skip(startFrom)
                .collect(Collectors.toList());
    }

}
