package io.github.cnadjim.eventflow.sample.domain.account.event;

import io.github.cnadjim.eventflow.annotation.AggregateIdentifier;

import java.time.LocalDate;

public record AccountCreatedEvent(@AggregateIdentifier String email, String password, String pseudonym, LocalDate birthDate) implements AccountEvent {

}
