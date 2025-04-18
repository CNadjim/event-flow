package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record CommandResult(String id,
                            Error error,
                            Object payload,
                            MessageResultTopic resultTopic) implements MessageResult<Command> {

    public CommandResult {
        if (StringUtils.isBlank(id)) throw new IllegalArgumentException("id cannot be empty");
        if (Objects.isNull(resultTopic)) throw new IllegalArgumentException("resultTopic cannot be null");
    }

    public static CommandResult success(Command command) {
        return new CommandResult(command.id(), null, command.aggregateId(), MessageResultTopic.create(command));
    }

    public static CommandResult failure(Command command, Error error) {
        return new CommandResult(command.id(), error, null, MessageResultTopic.create(command));
    }
}
