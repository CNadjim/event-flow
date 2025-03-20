package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.annotation.Stub;
import io.github.cnadjim.eventflow.core.domain.EventWrapper;
import org.apache.commons.collections.CollectionUtils;
import io.github.cnadjim.eventflow.core.spi.EventStore;

import java.util.ArrayList;
import java.util.Collections;
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

    private final ConcurrentMap<String, CopyOnWriteArrayList<EventWrapper>> eventsMap = new ConcurrentHashMap<>();

    @Override
    public void save(EventWrapper event) {
        if (nonNull(event)) {
            eventsMap.compute(event.aggregateId(), (k, existingList) -> {
                if (isNull(existingList)) {
                    existingList = new CopyOnWriteArrayList<>();
                }
                existingList.add(event);
                return existingList;
            });
        }
    }

    @Override
    public void saveAll(List<EventWrapper> events) {
        if (CollectionUtils.isEmpty(events)) {
            return;
        }

        for (EventWrapper event : events) {
            String key = event.aggregateId();
            eventsMap.compute(key, (k, existingList) -> {
                if (isNull(existingList)) {
                    existingList = new CopyOnWriteArrayList<>();
                }
                existingList.add(event);
                return existingList;
            });
        }
    }

    @Override
    public List<EventWrapper> findAllByAggregateIdOrderByTimestampAsc(String aggregateId) {
        return eventsMap.getOrDefault(aggregateId, new CopyOnWriteArrayList<>())
                .stream()
                .sorted(Comparator.comparing(EventWrapper::timestamp))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventWrapper> findAllByAggregateIdOrderByTimestampAscStartFrom(String aggregateId, int startFrom) {
        return eventsMap.getOrDefault(aggregateId, new CopyOnWriteArrayList<>())
                .stream()
                .sorted(Comparator.comparing(EventWrapper::timestamp))
                .skip(startFrom)
                .collect(Collectors.toList());
    }

}
