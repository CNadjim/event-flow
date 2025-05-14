package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.annotation.Stub;
import io.github.cnadjim.eventflow.core.domain.aggregate.Aggregate;
import io.github.cnadjim.eventflow.core.port.AggregateStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Stub
public class InMemoryAggregateStore implements AggregateStore {

    private final ConcurrentMap<String, CopyOnWriteArrayList<Aggregate>> aggregatesByTopicName = new ConcurrentHashMap<>();

    @Override
    public Optional<Aggregate> findTopByAggregateIdOrderByVersionDesc(String aggregateId) {
        return aggregatesByTopicName.getOrDefault(aggregateId, new CopyOnWriteArrayList<>())
                .stream()
                .max(Comparator.naturalOrder());
    }

    @Override
    public void save(Aggregate aggregate) {
        aggregatesByTopicName.compute(aggregate.aggregateId(), (topicName, existing) -> {
            if (Objects.isNull(existing)) {
                existing = new CopyOnWriteArrayList<>();
            }
            existing.add(aggregate);
            return existing;
        });
    }

    @Override
    public void deleteAllByAggregateId(String aggregateId) {
        aggregatesByTopicName.remove(aggregateId);
    }
}
