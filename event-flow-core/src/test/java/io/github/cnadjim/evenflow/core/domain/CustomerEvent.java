package io.github.cnadjim.evenflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.annotation.AggregateId;
import io.github.cnadjim.eventflow.core.domain.annotation.Topic;


@Topic("events.customer")
public interface CustomerEvent {

    record CustomerCreatedEvent(@AggregateId String id, String name) implements CustomerEvent {
    }

    record CustomerNameUpdatedEvent(@AggregateId String id, String newName) implements CustomerEvent {
    }

}
