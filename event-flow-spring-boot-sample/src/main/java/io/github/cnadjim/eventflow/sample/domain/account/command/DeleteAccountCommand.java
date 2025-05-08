package io.github.cnadjim.eventflow.sample.domain.account.command;

import io.github.cnadjim.eventflow.annotation.AggregateIdentifier;

public record DeleteAccountCommand(@AggregateIdentifier String email) implements AccountCommand {
}
