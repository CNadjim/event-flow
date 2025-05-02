package io.github.cnadjim.eventflow.core.domain.error;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

import static java.util.Objects.isNull;

public record InternalServerError(Instant timestamp, String message, Object details) implements Error {

    public static Integer INTERNAL_SERVER_ERROR_STATUS = 500;
    public static String INTERNAL_SERVER_ERROR_REASON_PHRASE = "Internal Server Error";

    public InternalServerError {
        if (isNull(timestamp)) throw new BadArgumentException("Timestamp cannot be null");
        if (StringUtils.isBlank(message)) throw new BadArgumentException("Message cannot be blank");
    }

    @Override
    public Integer status() {
        return INTERNAL_SERVER_ERROR_STATUS;
    }

    @Override
    public String reasonPhrase() {
        return INTERNAL_SERVER_ERROR_REASON_PHRASE;
    }

    public static InternalServerError create(String message) {
        return new InternalServerError(Instant.now(), message, null);
    }
}
