package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.Message;

public interface MessagePublisher<MESSAGE extends Message> {

    void subscribe(MessageSubscriber<MESSAGE> subscriber);
}
