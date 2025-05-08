package io.github.cnadjim.eventflow.sample.domain.account.command;

import io.github.cnadjim.eventflow.annotation.AggregateIdentifier;
import io.github.cnadjim.eventflow.sample.model.request.UpdateAccountBirthdayRequest;

import java.time.LocalDate;

public record UpdateAccountBirthDateCommand(@AggregateIdentifier String email, LocalDate newBirthDate) implements AccountCommand {

    public static UpdateAccountBirthDateCommand of(String email, UpdateAccountBirthdayRequest updateAccountBirthdayRequest) {
        return new UpdateAccountBirthDateCommand(email, updateAccountBirthdayRequest.newBirthday());
    }
}
