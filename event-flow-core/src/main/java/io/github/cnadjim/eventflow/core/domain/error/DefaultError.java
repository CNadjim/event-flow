package io.github.cnadjim.eventflow.core.domain.error;

import io.github.cnadjim.eventflow.core.domain.Error;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

import static java.util.Objects.isNull;

public record  DefaultError(Instant timestamp,
                           Integer status,
                           String reasonPhrase,
                           String message,
                           Object details) implements Error {

    public DefaultError {
        if (isNull(timestamp)) throw new IllegalArgumentException("Timestamp cannot be null");
        if (isNull(status)) throw new IllegalArgumentException("Status cannot be null");
        if (StringUtils.isBlank(reasonPhrase)) throw new IllegalArgumentException("ReasonPhrase cannot be blank");
        if (StringUtils.isBlank(message)) throw new IllegalArgumentException("Message cannot be blank");
    }

    public static DefaultError create(Integer status, String reasonPhrase, String message, Object details) {
        return new DefaultError(Instant.now(), status, reasonPhrase, message, details);
    }

    public static DefaultError create(Error error) {
        return new DefaultError(error.timestamp(), error.status(), error.reasonPhrase(), error.message(), error.details());
    }
}
