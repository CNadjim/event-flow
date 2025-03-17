package io.github.cnadjim.eventflow.sample.domain.account.event;

import io.github.cnadjim.eventflow.core.domain.annotation.AggregateId;

import java.time.LocalDate;

public record AccountBirthDateUpdatedEvent(@AggregateId String email, LocalDate newBirthDate) implements AccountEvent {

}
