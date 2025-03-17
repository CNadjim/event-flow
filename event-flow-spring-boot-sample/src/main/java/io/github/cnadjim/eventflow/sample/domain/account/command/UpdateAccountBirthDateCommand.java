package io.github.cnadjim.eventflow.sample.domain.account.command;

import io.github.cnadjim.eventflow.core.domain.annotation.AggregateId;

import java.time.LocalDate;

public record UpdateAccountBirthDateCommand(@AggregateId String email, LocalDate newBirthDate) implements AccountCommand {
}
