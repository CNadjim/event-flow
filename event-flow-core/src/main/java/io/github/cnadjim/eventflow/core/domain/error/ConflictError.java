package io.github.cnadjim.eventflow.core.domain.error;

import java.time.Instant;

public record ConflictError(Instant timestamp, String message, Object details) implements Error {

    public static Integer CONFLICT_ERROR_STATUS = 409;
    public static String CONFLICT_ERROR_REASON_PHRASE = "Conflict";

    @Override
    public Integer status() {
        return CONFLICT_ERROR_STATUS;
    }

    @Override
    public String reasonPhrase() {
        return CONFLICT_ERROR_REASON_PHRASE;
    }

    public static ConflictError create(String message) {
        return new ConflictError(Instant.now(), message, null);
    }
}
