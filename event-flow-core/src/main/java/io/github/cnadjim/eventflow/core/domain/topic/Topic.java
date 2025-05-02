package io.github.cnadjim.eventflow.core.domain.topic;

public sealed interface Topic permits MessageTopic, MessageResultTopic {

    String name();

    long retentionInMs();

    default int retentionInMsAsInt(){
        return (int) retentionInMs();
    }
}
