package io.github.cnadjim.eventflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;

public interface PayloadWrapper extends PayloadSupplier {

    Object payload();

    /*
    String TOPIC_FORMAT = "%s.%s";

    @Override
    default MessageType type() {
        if (this instanceof EventWrapper) {
            return MessageType.EVENT;
        }
        throw new IllegalStateException("Unexpected value: " + this);
    }

    @Override
    default int compareTo(PayloadWrapper otherMessage) {
        return timestamp().compareTo(otherMessage.timestamp());
    }

    @Override
    default String topic(){
        return String.format(TOPIC_FORMAT, type().label(), payload().getClass().getSimpleName());
    }*/
}
