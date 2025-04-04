package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.error.DefaultError;
import io.github.cnadjim.eventflow.core.spi.ErrorConverter;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.isNull;

public record MessageResult(String id,
                            Topic topic,
                            Object payload,
                            ResultStatus status,
                            Error error) implements Message {

    public static final String RESULT_SUFFIX = "_Result";

    public enum ResultStatus {
        SUCCESS,
        FAILURE,
    }

    public MessageResult {
        if (StringUtils.isBlank(id)) throw new IllegalArgumentException("id cannot be null");
        if (isNull(topic)) throw new IllegalArgumentException("topic cannot be null");
        if (isNull(status)) throw new IllegalArgumentException("status cannot be null");
        if (status.equals(ResultStatus.FAILURE) && isNull(error)) {
            throw new IllegalArgumentException("error cannot be null");
        }
    }

    public static MessageResult success(Message message) {
        return success(message, null);
    }

    public static MessageResult success(Message message, Object payload) {
        return new MessageResult(message.id(), resultTopic(message), payload, ResultStatus.SUCCESS, null);
    }

    public static MessageResult failure(Message message, Throwable throwable, ErrorConverter errorConverter) {
        final Error error = errorConverter.convert(throwable);
        return MessageResult.failure(message, error);
    }

    public static MessageResult failure(Message message, Error error) {
        return new MessageResult(message.id(), MessageResult.resultTopic(message), null, ResultStatus.FAILURE, DefaultError.create(error));
    }

    public static Topic resultTopic(Message message) {
        return Topic.create(message.payloadClassSimpleName() + RESULT_SUFFIX);
    }

}
