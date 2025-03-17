package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.core.ddd.Stub;
import io.github.cnadjim.eventflow.core.domain.Event;
import org.apache.commons.collections.CollectionUtils;
import io.github.cnadjim.eventflow.core.spi.EventStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Stub
public class InMemoryEventStore implements EventStore {

    private final ConcurrentMap<String, List<Event>> eventsMap = new ConcurrentHashMap<>();


    @Override
    public void save(Event event) {
        if (nonNull(event)) {
            eventsMap.compute(event.aggregateId(), (k, existingList) -> {
                if (isNull(existingList)) {
                    existingList = new ArrayList<>();
                }
                existingList.add(event);
                return existingList;
            });
        }
    }

    @Override
    public void saveAll(List<Event> events) {
        if (CollectionUtils.isEmpty(events)) {
            return;
        }

        for (Event event : events) {
            String key = event.aggregateId();
            eventsMap.compute(key, (k, existingList) -> {
                if (isNull(existingList)) {
                    existingList = new ArrayList<>();
                }
                existingList.add(event);
                return existingList;
            });
        }
    }

    @Override
    public List<Event> findAllByAggregateIdOrderByTimestampAsc(String aggregateId) {
        return this.eventsMap.getOrDefault(aggregateId, Collections.emptyList()).stream().sorted().collect(Collectors.toList());
    }

}
