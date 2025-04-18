package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record EventResult(String id,
                          Error error,
                          Object payload,
                          MessageResultTopic resultTopic) implements MessageResult<Event> {

    public EventResult {
        if (StringUtils.isBlank(id)) throw new IllegalArgumentException("id cannot be empty");
        if (Objects.isNull(resultTopic)) throw new IllegalArgumentException("resultTopic cannot be null");
    }

    public static EventResult success(Event event){
        return new EventResult(event.id(), null, null, MessageResultTopic.create(event));
    }

    public static EventResult failure(Event event, Error error){
        return new EventResult(event.id(), error, null, MessageResultTopic.create(event));
    }
}
