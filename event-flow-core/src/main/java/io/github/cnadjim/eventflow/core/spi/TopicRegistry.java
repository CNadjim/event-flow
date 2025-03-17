package io.github.cnadjim.eventflow.core.spi;

import io.github.cnadjim.eventflow.core.domain.supplier.MessageTypeSupplier.MessageType;

import java.util.Set;

public interface TopicRegistry {
    void addTopic(MessageType messageType, String topic);
    Set<String> getTopics(MessageType messageType);
}
