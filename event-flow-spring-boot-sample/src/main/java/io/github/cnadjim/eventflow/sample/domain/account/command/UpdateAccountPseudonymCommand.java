package io.github.cnadjim.eventflow.sample.domain.account.command;

import io.github.cnadjim.eventflow.annotation.AggregateId;
import io.github.cnadjim.eventflow.sample.dto.request.UpdateAccountPseudonymRequest;

public record UpdateAccountPseudonymCommand(@AggregateId String email, String newPseudonym) implements AccountCommand {



    public static UpdateAccountPseudonymCommand of(String email, UpdateAccountPseudonymRequest updateAccountPseudonymRequest) {
        return new UpdateAccountPseudonymCommand(
                email,
                updateAccountPseudonymRequest.newPseudonym()
        );
    }
}
