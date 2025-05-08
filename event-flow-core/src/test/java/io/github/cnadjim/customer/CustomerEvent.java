package io.github.cnadjim.customer;

import io.github.cnadjim.eventflow.annotation.AggregateIdentifier;

import java.time.LocalDate;


public interface CustomerEvent {

    record CustomerCreatedEvent(@AggregateIdentifier String id, String name) implements CustomerEvent {
    }

    record CustomerNameUpdatedEvent(@AggregateIdentifier String id, String newName) implements CustomerEvent {
    }

    record CustomerBirthdayUpdatedEvent(@AggregateIdentifier String id, LocalDate newBirthDay) implements CustomerEvent {
    }

}
