package io.github.cnadjim.eventflow.sample.domain.account.event;

import io.github.cnadjim.eventflow.annotation.AggregateIdentifier;

public record AccountDeletedEvent(@AggregateIdentifier String email) implements AccountEvent {

}
