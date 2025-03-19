package io.github.cnadjim.eventflow.sample.domain.account.command;

import io.github.cnadjim.eventflow.annotation.AggregateId;

public record UpdateAccountPseudonymCommand(@AggregateId String email, String newPseudonym) implements AccountCommand {
}
