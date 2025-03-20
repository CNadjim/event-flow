package io.github.cnadjim.eventflow.sample.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateAccountBirthdayRequest(@NotNull LocalDate newBirthday) {
}
