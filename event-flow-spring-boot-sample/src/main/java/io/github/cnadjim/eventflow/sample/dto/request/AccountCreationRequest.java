package io.github.cnadjim.eventflow.sample.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record AccountCreationRequest(@Email String email, @NotBlank String password, String pseudonym, LocalDate birthDate) {

}
