package io.github.cnadjim.eventflow.sample.domain.account.event;

import io.github.cnadjim.eventflow.annotation.AggregateIdentifier;

public record AccountPseudonymUpdatedEvent(@AggregateIdentifier String email, String newPseudonym) implements AccountEvent {

}
