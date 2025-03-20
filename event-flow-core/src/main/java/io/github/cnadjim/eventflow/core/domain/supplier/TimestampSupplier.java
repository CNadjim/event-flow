package io.github.cnadjim.eventflow.core.domain.supplier;

import java.time.Instant;

@FunctionalInterface
public interface TimestampSupplier {
    Instant timestamp();

    static Instant create(){
        return Instant.now();
    }
}
