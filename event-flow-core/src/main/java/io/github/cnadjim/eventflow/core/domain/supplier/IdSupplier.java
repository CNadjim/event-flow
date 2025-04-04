package io.github.cnadjim.eventflow.core.domain.supplier;

import java.util.UUID;

public interface IdSupplier {

    String id();

    static String create() {
        return UUID.randomUUID().toString();
    }
}
