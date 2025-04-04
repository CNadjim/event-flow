package io.github.cnadjim.eventflow.core.domain.flux;

import io.github.cnadjim.eventflow.core.domain.Message;

public interface MessageProcessor<MESSAGE extends Message> extends MessageSubscriber<MESSAGE>, MessagePublisher<MESSAGE> {
}
