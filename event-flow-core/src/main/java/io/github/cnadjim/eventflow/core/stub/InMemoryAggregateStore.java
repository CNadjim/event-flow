package io.github.cnadjim.eventflow.core.stub;

import io.github.cnadjim.eventflow.annotation.Stub;
import io.github.cnadjim.eventflow.core.domain.AggregateWrapper;
import io.github.cnadjim.eventflow.core.spi.AggregateStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Stub
public class InMemoryAggregateStore implements AggregateStore {
    private final ConcurrentMap<String, CopyOnWriteArrayList<AggregateWrapper>> aggregatesMap = new ConcurrentHashMap<>();

    @Override
    public Optional<AggregateWrapper> findTopByAggregateIdOrderByVersionDesc(String aggregateId) {
        return aggregatesMap.getOrDefault(aggregateId, new CopyOnWriteArrayList<>())
                .stream()
                .max(Comparator.naturalOrder());
    }

    @Override
    public void save(AggregateWrapper aggregate) {
        aggregatesMap.compute(aggregate.aggregateId(), (key, existing) -> {
            if (Objects.isNull(existing)) {
                existing = new CopyOnWriteArrayList<>();
            }
            existing.add(aggregate);
            return existing;
        });
    }
}
