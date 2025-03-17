package io.github.cnadjim.eventflow.sample.domain.account.event;

import io.github.cnadjim.eventflow.core.domain.annotation.AggregateId;

import java.time.LocalDate;

public record AccountCreatedEvent(@AggregateId String email, String password, String pseudonym, LocalDate birthDate) implements AccountEvent {

}
