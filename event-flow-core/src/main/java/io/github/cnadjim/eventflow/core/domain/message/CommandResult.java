package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;

public record CommandResult(String id,
                            Error error,
                            Object payload,
                            MessageResultTopic resultTopic) implements MessageResult<Command> {

    public static CommandResult success(Command command){
        return new CommandResult(command.id(), null, command.aggregateId(), MessageResultTopic.create(command));
    }

    public static CommandResult failure(Command command, Error error){
        return new CommandResult(command.id(), error, null, MessageResultTopic.create(command));
    }
}
