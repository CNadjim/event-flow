package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.Message;
import io.github.cnadjim.eventflow.core.domain.flux.MessageSubscriber;


public interface MessageBus {

    <MESSAGE extends Message> void publish(MESSAGE message);

    <MESSAGE extends Message> void subscribe(MessageSubscriber<MESSAGE> messageSubscriber);
}
