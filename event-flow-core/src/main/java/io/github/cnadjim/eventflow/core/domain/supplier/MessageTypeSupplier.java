package io.github.cnadjim.eventflow.core.domain.supplier;

public interface MessageTypeSupplier {
    enum MessageType {
        COMMAND,
        EVENT,
        QUERY,
    }

    MessageType type();
}
