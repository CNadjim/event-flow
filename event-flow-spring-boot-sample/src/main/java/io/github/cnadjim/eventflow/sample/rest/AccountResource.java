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
import io.github.cnadjim.eventflow.sample.dto.request.UpdateAccountBirthdayRequest;
import io.github.cnadjim.eventflow.sample.dto.request.UpdateAccountPseudonymRequest;
import io.github.cnadjim.eventflow.sample.dto.response.AccountCreatedResponse;
import io.github.cnadjim.eventflow.sample.dto.request.CreateAccountRequest;
import io.github.cnadjim.eventflow.sample.dto.response.AccountDeletedResponse;
import io.github.cnadjim.eventflow.sample.dto.response.AccountUpdatedResponse;
import io.github.cnadjim.eventflow.sample.dto.response.MessageResponse;
import io.github.cnadjim.eventflow.spring.starter.pagination.PageResponse;
import io.github.cnadjim.eventflow.spring.starter.pagination.SpringResponseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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

    @GetMapping
    public CompletableFuture<PageResponse<MongoAccountEntity>> findAllAccount() {
        return sendQuery.query(new FindAllAccountQuery(), SpringResponseType.pageOf(MongoAccountEntity.class)).thenApplyAsync(PageResponse::of);
    }

    @GetMapping("{email}")
    public CompletableFuture<MongoAccountEntity> findAccount(@PathVariable @Valid @Email String email) {
        return sendQuery.query(new FindAccountQuery(email), ResponseType.instanceOf(MongoAccountEntity.class));
    }

    @PostMapping
    public CompletableFuture<MessageResponse> createAccount(@RequestBody @Valid CreateAccountRequest accountCreationRequest) {
        return sendCommand.sendCommand(CreateAccountCommand.of(accountCreationRequest)).thenApplyAsync(AccountCreatedResponse::of);
    }

    @PutMapping("{email}/update-pseudonym")
    public CompletableFuture<MessageResponse> updatePseudonym(@PathVariable @Valid @Email String email, @Valid @RequestBody UpdateAccountPseudonymRequest updateAccountPseudonymRequest) {
        return sendCommand.sendCommand(UpdateAccountPseudonymCommand.of(email, updateAccountPseudonymRequest)).thenApplyAsync(AccountUpdatedResponse::of);
    }

    @PutMapping("{email}/update-birth-date")
    public CompletableFuture<MessageResponse> updateBirthDate(@PathVariable @Valid @Email String email, @Valid @RequestBody UpdateAccountBirthdayRequest updateAccountBirthdayRequest) {
        return sendCommand.sendCommand(UpdateAccountBirthDateCommand.of(email, updateAccountBirthdayRequest)).thenApplyAsync(AccountUpdatedResponse::of);
    }

    @DeleteMapping("{email}")
    public CompletableFuture<MessageResponse> deleteAccount(@PathVariable @Valid @Email String email) {
        return sendCommand.sendCommand(new DeleteAccountCommand(email)).thenApplyAsync(AccountDeletedResponse::of);
    }

}
