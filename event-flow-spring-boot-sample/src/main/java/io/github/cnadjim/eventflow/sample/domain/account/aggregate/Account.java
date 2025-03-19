package io.github.cnadjim.eventflow.sample.domain.account.aggregate;

import io.github.cnadjim.eventflow.annotation.Aggregate;
import io.github.cnadjim.eventflow.annotation.AggregateId;
import io.github.cnadjim.eventflow.annotation.ApplyEvent;
import io.github.cnadjim.eventflow.annotation.HandleCommand;
import io.github.cnadjim.eventflow.sample.domain.account.command.CreateAccountCommand;
import io.github.cnadjim.eventflow.sample.domain.account.command.DeleteAccountCommand;
import io.github.cnadjim.eventflow.sample.domain.account.command.UpdateAccountBirthDateCommand;
import io.github.cnadjim.eventflow.sample.domain.account.command.UpdateAccountPseudonymCommand;
import io.github.cnadjim.eventflow.sample.domain.account.event.*;
import io.github.cnadjim.eventflow.sample.domain.account.exception.AccountAlreadyExistException;
import io.github.cnadjim.eventflow.sample.domain.account.exception.AccountNotFoundException;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Aggregate
public class Account {

    @AggregateId
    private String email;
    private String password;
    private String pseudonym;
    private LocalDate birthDate;

    @HandleCommand
    public AccountEvent handle(CreateAccountCommand command) {
        return new AccountCreatedEvent(command.email(), command.password(), command.pseudonym(), command.birthDate());
    }

    @HandleCommand
    public AccountEvent handle(UpdateAccountPseudonymCommand command) {
        return new AccountPseudonymUpdatedEvent(command.email(), command.newPseudonym());
    }

    @HandleCommand
    public AccountEvent handle(UpdateAccountBirthDateCommand command) {
        return new AccountBirthDateUpdatedEvent(command.email(), command.newBirthDate());
    }


    @HandleCommand
    public AccountEvent handle(DeleteAccountCommand command) {
        return new AccountDeletedEvent(command.email());
    }

    @ApplyEvent
    public Account applyEvent(AccountDeletedEvent event, Account account) {
        if (isNull(account)) {
            throw new AccountNotFoundException(event.email());
        }

        return null;
    }

    @ApplyEvent
    public Account applyEvent(AccountPseudonymUpdatedEvent event, Account account) {
        if (isNull(account)) {
            throw new AccountNotFoundException(event.email());
        }

        return AccountBuilder.from(account)
                .pseudonym(event.newPseudonym())
                .build();
    }

    @ApplyEvent
    public Account applyEvent(AccountBirthDateUpdatedEvent event, Account account) {
        if (isNull(account)) {
            throw new AccountNotFoundException(event.email());
        }

        return AccountBuilder.from(account)
                .birthDate(event.newBirthDate())
                .build();
    }

    @ApplyEvent
    public Account applyEvent(AccountCreatedEvent event, Account account) {
        if (nonNull(account)) {
            throw new AccountAlreadyExistException(event.email());
        }

        return AccountBuilder.builder()
                .email(event.email())
                .password(event.password())
                .pseudonym(event.pseudonym())
                .birthDate(event.birthDate())
                .build();
    }
}
