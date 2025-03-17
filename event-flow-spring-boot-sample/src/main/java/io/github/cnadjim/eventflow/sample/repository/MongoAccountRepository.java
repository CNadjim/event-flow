package io.github.cnadjim.eventflow.sample.repository;

import io.github.cnadjim.eventflow.sample.domain.account.entity.MongoAccountEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoAccountRepository extends MongoRepository<MongoAccountEntity, String> {
}
