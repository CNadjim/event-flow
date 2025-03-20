package io.github.cnadjim.eventflow.sample.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountPseudonymRequest(@NotBlank String newPseudonym) {
}
