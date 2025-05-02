package io.github.cnadjim.eventflow.core.domain.topic;

import java.util.concurrent.TimeUnit;

public record MessageResultTopic(String name) implements Topic {

    private static final String RESULT_SUFFIX = "_Result";

    @Override
    public long retentionInMs() {
        return TimeUnit.MINUTES.toMillis(1);
    }


    @Override
    public String name() {
        return name + RESULT_SUFFIX;
    }
}
