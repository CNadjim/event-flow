package io.github.cnadjim.eventflow.sample.domain.account.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "account-store")
public class MongoAccountEntity {
    @Id
    private String email;
    private String password;
    private String pseudonym;
    private LocalDate birthDate;
}
