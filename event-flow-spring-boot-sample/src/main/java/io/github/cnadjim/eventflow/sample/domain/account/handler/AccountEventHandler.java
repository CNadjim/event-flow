package io.github.cnadjim.eventflow.sample.domain.account.handler;

import io.github.cnadjim.eventflow.annotation.EventHandler;
import io.github.cnadjim.eventflow.sample.domain.account.entity.MongoAccountEntity;
import io.github.cnadjim.eventflow.sample.domain.account.event.AccountBirthDateUpdatedEvent;
import io.github.cnadjim.eventflow.sample.domain.account.event.AccountCreatedEvent;
import io.github.cnadjim.eventflow.sample.domain.account.event.AccountDeletedEvent;
import io.github.cnadjim.eventflow.sample.domain.account.event.AccountPseudonymUpdatedEvent;
import io.github.cnadjim.eventflow.sample.domain.account.exception.AccountNotFoundException;
import io.github.cnadjim.eventflow.sample.repository.MongoAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventHandler {

    private final MongoAccountRepository mongoAccountRepository;

    @EventHandler
    @Transactional
    public void handle(AccountCreatedEvent event) {
        log.info("Account created: {}", event);

        final MongoAccountEntity mongoAccountEntity = new MongoAccountEntity();
        mongoAccountEntity.setEmail(event.email());
        mongoAccountEntity.setPassword(event.password());
        mongoAccountEntity.setBirthDate(event.birthDate());
        mongoAccountEntity.setPseudonym(event.pseudonym());

        mongoAccountRepository.save(mongoAccountEntity);
    }

    @EventHandler
    @Transactional
    public void handle(AccountPseudonymUpdatedEvent event) {
        log.info("Account pseudonym updated: {}", event);
        MongoAccountEntity account = getAccountEntity(event.email());
        account.setPseudonym(event.newPseudonym());
        mongoAccountRepository.save(account);
    }

    @EventHandler
    @Transactional
    public void handle(AccountBirthDateUpdatedEvent event) {
        log.info("Account birth date updated: {}", event);
        MongoAccountEntity account = getAccountEntity(event.email());
        account.setBirthDate(event.newBirthDate());
        mongoAccountRepository.save(account);
    }

    @EventHandler
    @Transactional
    public void handle(AccountDeletedEvent event) {
        log.info("Account deleted: {}", event);
        mongoAccountRepository.deleteById(event.email());
    }

    private MongoAccountEntity getAccountEntity(String email) {
       return mongoAccountRepository.findById(email).orElseThrow(() -> new AccountNotFoundException(email));
    }
}
