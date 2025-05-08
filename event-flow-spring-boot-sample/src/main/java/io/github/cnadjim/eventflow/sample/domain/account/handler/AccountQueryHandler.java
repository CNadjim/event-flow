package io.github.cnadjim.eventflow.sample.domain.account.handler;

import io.github.cnadjim.eventflow.annotation.QueryHandler;
import io.github.cnadjim.eventflow.core.domain.pagination.Page;
import io.github.cnadjim.eventflow.sample.domain.account.entity.MongoAccountEntity;
import io.github.cnadjim.eventflow.sample.domain.account.exception.AccountNotFoundException;
import io.github.cnadjim.eventflow.sample.domain.account.query.FindAccountQuery;
import io.github.cnadjim.eventflow.sample.domain.account.query.FindAllAccountQuery;
import io.github.cnadjim.eventflow.sample.repository.MongoAccountRepository;
import io.github.cnadjim.eventflow.spring.starter.utils.SpringAdaptors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountQueryHandler {
    private final MongoAccountRepository mongoAccountRepository;

    @QueryHandler
    public MongoAccountEntity handle(FindAccountQuery query) {
        return mongoAccountRepository.findById(query.email()).orElseThrow(() -> new AccountNotFoundException(query.email()));
    }

    @QueryHandler
    public Page<MongoAccountEntity> handle(FindAllAccountQuery query) {
        return SpringAdaptors.toPage(mongoAccountRepository.findAll(PageRequest.ofSize(100)));
    }
}
