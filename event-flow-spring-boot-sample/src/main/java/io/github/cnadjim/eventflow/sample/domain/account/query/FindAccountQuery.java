package io.github.cnadjim.eventflow.sample.domain.account.query;

public record FindAccountQuery(String email) implements AccountQuery {
}
