package io.github.cnadjim.eventflow.sample.domain.account.aggregate;

import io.github.cnadjim.eventflow.annotation.Aggregate;
import io.github.cnadjim.eventflow.annotation.AggregateIdentifier;
import io.github.cnadjim.eventflow.annotation.EventSourcingHandler;
import io.github.cnadjim.eventflow.annotation.CommandHandler;
import io.github.cnadjim.eventflow.core.domain.aggregate.AggregateLifecycle;
import io.github.cnadjim.eventflow.sample.domain.account.command.CreateAccountCommand;
import io.github.cnadjim.eventflow.sample.domain.account.command.DeleteAccountCommand;
import io.github.cnadjim.eventflow.sample.domain.account.command.UpdateAccountBirthDateCommand;
import io.github.cnadjim.eventflow.sample.domain.account.command.UpdateAccountPseudonymCommand;
import io.github.cnadjim.eventflow.sample.domain.account.event.*;
import io.github.cnadjim.eventflow.sample.domain.account.exception.AccountAlreadyExistException;
import io.github.cnadjim.eventflow.sample.domain.account.exception.AccountNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Aggregate
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Account {

    @AggregateIdentifier
    private String email;
    private String password;
    private String pseudonym;
    private LocalDate birthDate;

    @CommandHandler
    public AccountEvent handle(CreateAccountCommand command) {
        if (StringUtils.isNotBlank(email)) {
            throw new AccountAlreadyExistException(command.email());
        }

        return new AccountCreatedEvent(command.email(), command.password(), command.pseudonym(), command.birthDate());
    }

    @CommandHandler
    public AccountEvent handle(UpdateAccountPseudonymCommand command) {
        if (StringUtils.isBlank(email)) {
            throw new AccountNotFoundException(command.email());
        }
        return new AccountPseudonymUpdatedEvent(command.email(), command.newPseudonym());
    }

    @CommandHandler
    public AccountEvent handle(UpdateAccountBirthDateCommand command) {
        if (StringUtils.isBlank(email)) {
            throw new AccountNotFoundException(command.email());
        }
        return new AccountBirthDateUpdatedEvent(command.email(), command.newBirthDate());
    }

    @CommandHandler
    public AccountEvent handle(DeleteAccountCommand command) {
        if (StringUtils.isBlank(email)) {
            throw new AccountNotFoundException(command.email());
        }

        return new AccountDeletedEvent(command.email());
    }

    @EventSourcingHandler
    public void applyEvent(AccountDeletedEvent event) {
        AggregateLifecycle.markDeleted();
    }

    @EventSourcingHandler
    public void applyEvent(AccountPseudonymUpdatedEvent event) {
        pseudonym = event.newPseudonym();
    }

    @EventSourcingHandler
    public void applyEvent(AccountBirthDateUpdatedEvent event) {
        birthDate = event.newBirthDate();
    }

    @EventSourcingHandler
    public void applyEvent(AccountCreatedEvent event) {
        email = event.email();
        password = event.password();
        pseudonym = event.pseudonym();
        birthDate = event.birthDate();
    }
}
