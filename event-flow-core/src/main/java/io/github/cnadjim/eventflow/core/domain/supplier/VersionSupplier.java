package io.github.cnadjim.eventflow.core.domain.supplier;

@FunctionalInterface
public interface VersionSupplier {

    Long version();

    static Long create() {
        return 0L;
    }
}
