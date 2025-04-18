package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;

public record EventResult(String id,
                          Error error,
                          Object payload,
                          MessageResultTopic resultTopic) implements MessageResult<Event> {

    public static EventResult success(Event event){
        return new EventResult(event.id(), null, null, MessageResultTopic.create(event));
    }

    public static EventResult failure(Event event, Error error){
        return new EventResult(event.id(), error, null, MessageResultTopic.create(event));
    }
}
