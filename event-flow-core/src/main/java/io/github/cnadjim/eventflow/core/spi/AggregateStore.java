package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.Aggregate;

import java.util.Optional;

public interface AggregateStore {
    void save(Aggregate aggregateWrapper);

    void deleteAllByAggregateId(String aggregateId);

    Optional<Aggregate> findTopByAggregateIdOrderByVersionDesc(String aggregateId);
}
