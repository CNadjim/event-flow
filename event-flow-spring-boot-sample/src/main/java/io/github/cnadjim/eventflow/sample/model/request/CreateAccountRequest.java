package io.github.cnadjim.eventflow.sample.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateAccountRequest(@Email String email, @NotBlank String password, String pseudonym, LocalDate birthDate) {

}
