package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.AggregateWrapper;

import java.util.Optional;

public interface AggregateStore {
    void save(AggregateWrapper aggregateWrapper);
    Optional<AggregateWrapper> findTopByAggregateIdOrderByVersionDesc(String aggregateId);
}
