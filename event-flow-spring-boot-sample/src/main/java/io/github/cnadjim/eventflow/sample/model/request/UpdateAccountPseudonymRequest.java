package io.github.cnadjim.eventflow.sample.model.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountPseudonymRequest(@NotBlank String newPseudonym) {
}
