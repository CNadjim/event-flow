package io.github.cnadjim.eventflow.sample.dto.response;

public record AccountUpdatedResponse(String message) {

    public static AccountUpdatedResponse of(String email) {
        return new AccountUpdatedResponse(String.format("Account with the email %s has been successfully updated.", email));
    }
}
