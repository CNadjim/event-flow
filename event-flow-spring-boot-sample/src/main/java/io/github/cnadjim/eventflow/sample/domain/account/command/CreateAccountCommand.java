package io.github.cnadjim.eventflow.sample.domain.account.command;

import io.github.cnadjim.eventflow.annotation.AggregateIdentifier;
import io.github.cnadjim.eventflow.sample.model.request.CreateAccountRequest;

import java.time.LocalDate;

public record CreateAccountCommand(@AggregateIdentifier String email, String password, String pseudonym, LocalDate birthDate) implements AccountCommand {

    public static CreateAccountCommand of(CreateAccountRequest accountCreationRequest) {
        return new CreateAccountCommand(
                accountCreationRequest.email(),
                accountCreationRequest.password(),
                accountCreationRequest.pseudonym(),
                accountCreationRequest.birthDate()
        );
    }
}
