package io.github.cnadjim.eventflow.sample.domain.account.command;

import io.github.cnadjim.eventflow.core.domain.annotation.AggregateId;

public record DeleteAccountCommand(@AggregateId String email) implements AccountCommand {
}
