package io.github.cnadjim.eventflow.sample.domain.account.event;

import io.github.cnadjim.eventflow.core.domain.annotation.AggregateId;

public record AccountDeletedEvent(@AggregateId String email) implements AccountEvent {

}
