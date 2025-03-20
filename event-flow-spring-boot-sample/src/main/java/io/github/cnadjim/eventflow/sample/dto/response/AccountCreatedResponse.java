package io.github.cnadjim.eventflow.sample.dto.response;

public record AccountCreatedResponse(String message) implements MessageResponse {

    public static AccountCreatedResponse of(String email) {
        return new AccountCreatedResponse(String.format("Account with the email %s has been successfully created.", email));
    }
}
