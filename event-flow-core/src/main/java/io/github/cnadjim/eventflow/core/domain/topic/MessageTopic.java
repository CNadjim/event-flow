package io.github.cnadjim.eventflow.core.domain.topic;

import java.util.concurrent.TimeUnit;

public record MessageTopic(String name) implements Topic {

    @Override
    public long retentionInMs() {
        return TimeUnit.DAYS.toMillis(1);
    }
}
