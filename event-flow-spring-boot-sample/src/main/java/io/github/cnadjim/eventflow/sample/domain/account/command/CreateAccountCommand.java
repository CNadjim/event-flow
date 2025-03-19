package io.github.cnadjim.eventflow.sample.domain.account.command;

import io.github.cnadjim.eventflow.annotation.AggregateId;
import io.github.cnadjim.eventflow.sample.dto.request.AccountCreationRequest;

import java.time.LocalDate;

public record CreateAccountCommand(@AggregateId String email, String password, String pseudonym, LocalDate birthDate) implements AccountCommand {

    public static CreateAccountCommand of(AccountCreationRequest accountCreationRequest) {
        return new CreateAccountCommand(
                accountCreationRequest.email(),
                accountCreationRequest.password(),
                accountCreationRequest.pseudonym(),
                accountCreationRequest.birthDate()
        );
    }
}
