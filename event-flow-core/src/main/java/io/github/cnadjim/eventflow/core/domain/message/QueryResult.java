package io.github.cnadjim.eventflow.core.domain.message;

import io.github.cnadjim.eventflow.core.domain.error.Error;
import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import io.github.cnadjim.eventflow.core.domain.topic.MessageResultTopic;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public record QueryResult(String id,
                          Error error,
                          Object payload,
                          MessageResultTopic resultTopic) implements MessageResult<Query> {

    public QueryResult {
        if (StringUtils.isBlank(id)) throw new BadArgumentException("id cannot be empty");
        if (Objects.isNull(resultTopic)) throw new BadArgumentException("resultTopic cannot be null");
    }

    public static QueryResult success(Query query, Object result){
        return new QueryResult(query.id(), null, result, MessageResultTopic.create(query));
    }

    public static QueryResult failure(Query query, Error error){
        return new QueryResult(query.id(), error, null, MessageResultTopic.create(query));
    }
}
