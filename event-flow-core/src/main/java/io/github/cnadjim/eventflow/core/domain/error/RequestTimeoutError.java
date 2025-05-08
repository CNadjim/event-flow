package io.github.cnadjim.eventflow.core.domain.error;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

import static java.util.Objects.isNull;

public record RequestTimeoutError(Instant timestamp, String message, Object details) implements Error {

    public static Integer REQUEST_TIMEOUT_ERROR_STATUS = 408;
    public static String REQUEST_TIMEOUT_ERROR_REASON_PHRASE = "Request Timeout";

    public RequestTimeoutError {
        if (isNull(timestamp)) throw new BadArgumentException("Timestamp cannot be null");
        if (StringUtils.isBlank(message)) throw new BadArgumentException("Message cannot be blank");
    }

    @Override
    public Integer status() {
        return REQUEST_TIMEOUT_ERROR_STATUS;
    }

    @Override
    public String reasonPhrase() {
        return REQUEST_TIMEOUT_ERROR_REASON_PHRASE;
    }

    public static RequestTimeoutError create(String message) {
        return new RequestTimeoutError(Instant.now(), message, null);
    }
}
