package io.github.cnadjim.eventflow.core.domain.supplier;

@FunctionalInterface
public interface PayloadSupplier {
    String PAYLOAD_FIELD = "payload";

    Object payload();

    default Class<?> payloadClass() {
        return payload().getClass();
    }
}
