package io.github.cnadjim.eventflow.sample.domain.account.exception;

import io.github.cnadjim.eventflow.spring.starter.exception.RestException;
import org.springframework.http.HttpStatus;

public class AccountAlreadyExistException extends RestException {
    public AccountAlreadyExistException(String email) {
        super(HttpStatus.CONFLICT, String.format("A Account is already registered with email : : %s", email));
    }
}
