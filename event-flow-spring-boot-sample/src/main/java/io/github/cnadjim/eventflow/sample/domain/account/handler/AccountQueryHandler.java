package io.github.cnadjim.eventflow.sample.domain.account.handler;

import io.github.cnadjim.eventflow.annotation.HandleQuery;
import io.github.cnadjim.eventflow.core.domain.Page;
import io.github.cnadjim.eventflow.sample.domain.account.entity.MongoAccountEntity;
import io.github.cnadjim.eventflow.sample.domain.account.exception.AccountNotFoundException;
import io.github.cnadjim.eventflow.sample.domain.account.query.FindAccountQuery;
import io.github.cnadjim.eventflow.sample.domain.account.query.FindAllAccountQuery;
import io.github.cnadjim.eventflow.sample.repository.MongoAccountRepository;
import io.github.cnadjim.eventflow.spring.starter.pagination.PageAdaptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return PageAdaptor.ofSpringPage(mongoAccountRepository.findAll(PageRequest.ofSize(100)));
    }
}
