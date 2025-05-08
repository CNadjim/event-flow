package io.github.cnadjim.eventflow.sample.domain.account.command;

import io.github.cnadjim.eventflow.annotation.AggregateIdentifier;
import io.github.cnadjim.eventflow.sample.model.request.UpdateAccountPseudonymRequest;

public record UpdateAccountPseudonymCommand(@AggregateIdentifier String email, String newPseudonym) implements AccountCommand {



    public static UpdateAccountPseudonymCommand of(String email, UpdateAccountPseudonymRequest updateAccountPseudonymRequest) {
        return new UpdateAccountPseudonymCommand(
                email,
                updateAccountPseudonymRequest.newPseudonym()
        );
    }
}
