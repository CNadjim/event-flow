package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import io.github.cnadjim.eventflow.core.domain.supplier.TimestampSupplier;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;
import io.github.cnadjim.eventflow.core.domain.topic.Topic;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public record MessageResult(String id,
                            Instant timestamp,
                            MessageResultTopic topic,
                            Error error,
                            Object payload) implements Message {

    public MessageResult {
        if (isNull(topic)) throw new BadArgumentException("topic cannot be null");
        if (StringUtils.isBlank(id)) throw new BadArgumentException("id cannot be empty");
    }

    public static MessageResult success(Message message, Object payload) {
        return new MessageResult(message.id(), TimestampSupplier.create(), new MessageResultTopic(message.topic().name()), null, payload);
    }

    public static MessageResult failure(Message message, Error error) {
        return new MessageResult(message.id(), TimestampSupplier.create(), new MessageResultTopic(message.topic().name()), error, null);
    }

    public boolean isSuccess() {
        return isNull(error);
    }

    public boolean isFailure() {
        return nonNull(error);
    }

}
