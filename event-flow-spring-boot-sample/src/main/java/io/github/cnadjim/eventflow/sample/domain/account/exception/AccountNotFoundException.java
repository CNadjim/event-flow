package io.github.cnadjim.eventflow.sample.domain.account.exception;

import io.github.cnadjim.eventflow.spring.starter.exception.RestException;
import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends RestException {
    public AccountNotFoundException(String email) {
        super(HttpStatus.NOT_FOUND, String.format("No account registered with email : %s", email));
    }
}
