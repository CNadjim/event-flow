package io.github.cnadjim.eventflow.sample.domain.account.event;

import io.github.cnadjim.eventflow.annotation.AggregateId;

public record AccountPseudonymUpdatedEvent(@AggregateId String email, String newPseudonym) implements AccountEvent {

}
