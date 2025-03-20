package io.github.cnadjim.eventflow.core.domain.supplier;

@FunctionalInterface
public interface VersionSupplier {

    String VERSION_FIELD = "version";

    Long version();

    static Long create() {
        return 0L;
    }
}
