package io.github.cnadjim.eventflow.core.domain;

import java.util.concurrent.TimeUnit;

import static io.github.cnadjim.eventflow.core.domain.MessageResult.RESULT_SUFFIX;

public record Topic(String name) {

    public enum TopicType {
        MESSAGE,
        MESSAGE_RESULT
    }

    public TopicType type() {
        if (name.contains(RESULT_SUFFIX)) {
            return TopicType.MESSAGE_RESULT;
        } else {
            return TopicType.MESSAGE;
        }
    }

    public long retentionInMs() {
        return switch (type()) {
            case MESSAGE -> TimeUnit.DAYS.toMillis(1);
            case MESSAGE_RESULT -> TimeUnit.HOURS.toMillis(1);
        };
    }

    public static Topic create(String name) {
        return new Topic(name);
    }
}
