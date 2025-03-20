package io.github.cnadjim.evenflow.core.domain;

import io.github.cnadjim.eventflow.annotation.AggregateId;
import io.github.cnadjim.eventflow.annotation.Topic;

import java.time.LocalDate;


public interface CustomerEvent {

    record CustomerCreatedEvent(@AggregateId String id, String name) implements CustomerEvent {
    }

    record CustomerNameUpdatedEvent(@AggregateId String id, String newName) implements CustomerEvent {
    }

    record CustomerBirthdayUpdatedEvent(@AggregateId String id, LocalDate newBirthDay) implements CustomerEvent {
    }

}
