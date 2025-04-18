package io.github.cnadjim.eventflow.core.domain.error;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

import static java.util.Objects.isNull;

public record NotFoundError(Instant timestamp, String message) implements Error {

    public static Integer NOT_FOUND_STATUS = 404;
    public static String NOT_FOUND_REASON_PHRASE = "Not Found";

    public NotFoundError {
        if (isNull(timestamp)) throw new BadArgumentException("Timestamp cannot be null");
        if (StringUtils.isBlank(message)) throw new BadArgumentException("Message cannot be blank");
    }

    public NotFoundError(String message) {
        this(Instant.now(), message);
    }

    @Override
    public Integer status() {
        return NOT_FOUND_STATUS;
    }

    @Override
    public String reasonPhrase() {
        return NOT_FOUND_REASON_PHRASE;
    }

    @Override
    public Object details() {
        return null;
    }

    public static NotFoundError create(String message) {
        return new NotFoundError(message);
    }
}
