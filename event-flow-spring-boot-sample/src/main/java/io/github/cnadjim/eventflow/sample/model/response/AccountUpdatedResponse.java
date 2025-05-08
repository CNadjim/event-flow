package io.github.cnadjim.eventflow.sample.model.response;

public record AccountUpdatedResponse(String message) implements MessageResponse {

    public static AccountUpdatedResponse of(String email) {
        return new AccountUpdatedResponse(String.format("Account with the email %s has been successfully updated.", email));
    }
}
