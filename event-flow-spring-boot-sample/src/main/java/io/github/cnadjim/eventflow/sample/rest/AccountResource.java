package io.github.cnadjim.eventflow.sample.rest;


import io.github.cnadjim.eventflow.core.api.SendCommand;
import io.github.cnadjim.eventflow.core.api.SendQuery;
import io.github.cnadjim.eventflow.core.domain.ResponseType;
import io.github.cnadjim.eventflow.sample.domain.account.command.CreateAccountCommand;
import io.github.cnadjim.eventflow.sample.domain.account.command.DeleteAccountCommand;
import io.github.cnadjim.eventflow.sample.domain.account.command.UpdateAccountBirthDateCommand;
import io.github.cnadjim.eventflow.sample.domain.account.command.UpdateAccountPseudonymCommand;
import io.github.cnadjim.eventflow.sample.domain.account.entity.MongoAccountEntity;
import io.github.cnadjim.eventflow.sample.domain.account.query.FindAccountQuery;
import io.github.cnadjim.eventflow.sample.domain.account.query.FindAllAccountQuery;
import io.github.cnadjim.eventflow.sample.dto.response.AccountCreatedResponse;
import io.github.cnadjim.eventflow.sample.dto.request.AccountCreationRequest;
import io.github.cnadjim.eventflow.spring.starter.pagination.SpringResponseType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/account")
public class AccountResource {

    private final SendQuery sendQuery;
    private final SendCommand sendCommand;

    @PostMapping
    public CompletableFuture<AccountCreatedResponse> createAccount(@RequestBody @Valid AccountCreationRequest accountCreationRequest) {
        return sendCommand.sendCommand(CreateAccountCommand.of(accountCreationRequest)).thenApplyAsync(AccountCreatedResponse::of);
    }

    @GetMapping
    public CompletableFuture<Page<MongoAccountEntity>> findAllAccount() {
        return sendQuery.query(new FindAllAccountQuery(), SpringResponseType.pageOf(MongoAccountEntity.class));
    }

    @GetMapping("{email}")
    public CompletableFuture<MongoAccountEntity> findAccount(@PathVariable String email) {
        return sendQuery.query(new FindAccountQuery(email), ResponseType.instanceOf(MongoAccountEntity.class));
    }

    @DeleteMapping("{email}")
    public CompletableFuture<String> deleteAccount(@PathVariable String email) {
        return sendCommand.sendCommand(new DeleteAccountCommand(email));
    }

    @PutMapping("{email}/update-pseudonym")
    public CompletableFuture<String> updatePseudonym(@PathVariable String email, @RequestBody UpdateAccountPseudonymCommand updateAccountPseudonymCommand) {
        return sendCommand.sendCommand(updateAccountPseudonymCommand);
    }

    @PutMapping("{email}/update-birth-date")
    public CompletableFuture<String> updateBirthDate(@PathVariable String email, @RequestBody UpdateAccountBirthDateCommand updateAccountBirthDateCommand) {
        return sendCommand.sendCommand(updateAccountBirthDateCommand);
    }

}
