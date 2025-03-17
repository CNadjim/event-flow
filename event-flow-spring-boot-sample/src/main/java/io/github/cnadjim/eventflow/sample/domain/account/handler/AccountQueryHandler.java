package io.github.cnadjim.eventflow.sample.domain.account.handler;

import io.github.cnadjim.eventflow.core.domain.annotation.HandleQuery;
import io.github.cnadjim.eventflow.sample.domain.account.entity.MongoAccountEntity;
import io.github.cnadjim.eventflow.sample.domain.account.exception.AccountNotFoundException;
import io.github.cnadjim.eventflow.sample.domain.account.query.FindAccountQuery;
import io.github.cnadjim.eventflow.sample.domain.account.query.FindAllAccountQuery;
import io.github.cnadjim.eventflow.sample.repository.MongoAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountQueryHandler {
    private final MongoAccountRepository mongoAccountRepository;

    @HandleQuery
    public MongoAccountEntity handle(FindAccountQuery query) {
        return mongoAccountRepository.findById(query.email()).orElseThrow(() -> new AccountNotFoundException(query.email()));
    }

    @HandleQuery
    public Page<MongoAccountEntity> handle(FindAllAccountQuery query) {
        return mongoAccountRepository.findAll(PageRequest.ofSize(10));
    }
}
