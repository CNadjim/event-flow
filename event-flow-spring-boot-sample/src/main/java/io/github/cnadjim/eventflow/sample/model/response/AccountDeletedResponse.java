package io.github.cnadjim.eventflow.sample.model.response;

public record AccountDeletedResponse(String message) implements MessageResponse {

    public static AccountDeletedResponse of(String email) {
        return new AccountDeletedResponse(String.format("Account with the email %s has been successfully deleted.", email));
    }
}
