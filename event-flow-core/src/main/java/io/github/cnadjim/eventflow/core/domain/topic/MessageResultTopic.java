package io.github.cnadjim.eventflow.core.domain.topic;

import io.github.cnadjim.eventflow.core.domain.message.Message;

import java.util.concurrent.TimeUnit;

public record MessageResultTopic(String name) implements Topic {

    private static final String RESULT_SUFFIX = "_Result";

    @Override
    public long retentionInMs() {
        return TimeUnit.HOURS.toMillis(1);
    }

    public static MessageResultTopic create(Message message) {
        final Topic topic = message.topic();
        if (topic instanceof MessageTopic) {
            return create((MessageTopic) topic);
        } else {
            throw new IllegalArgumentException("");
        }
    }

    public static MessageResultTopic create(MessageTopic messageTopic) {
        return new MessageResultTopic(messageTopic.name() + RESULT_SUFFIX);
    }
}
