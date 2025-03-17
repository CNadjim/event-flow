package io.github.cnadjim.eventflow.sample.dto.response;

public record AccountCreatedResponse(String message) {

    public static AccountCreatedResponse of(String email) {
        return new AccountCreatedResponse(String.format("A account with the email %s has been successfully created.", email));
    }
}
