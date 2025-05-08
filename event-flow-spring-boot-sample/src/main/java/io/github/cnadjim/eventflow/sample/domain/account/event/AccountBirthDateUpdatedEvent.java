package io.github.cnadjim.eventflow.sample.domain.account.event;

import io.github.cnadjim.eventflow.annotation.AggregateIdentifier;

import java.time.LocalDate;

public record AccountBirthDateUpdatedEvent(@AggregateIdentifier String email, LocalDate newBirthDate) implements AccountEvent {

}
