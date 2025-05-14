package io.github.cnadjim.eventflow.core.domain.aggregate;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import io.github.cnadjim.eventflow.core.domain.supplier.AggregateIdSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.PayloadSupplier;
import io.github.cnadjim.eventflow.core.domain.supplier.VersionSupplier;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.isNull;

/**
 * Represents an aggregate in the event flow system.
 * An aggregate is a cluster of domain objects that can be treated as a single unit.
 * It encapsulates the state of an entity and ensures its consistency.
 *
 * @param version     The version of the aggregate, used for optimistic concurrency control
 * @param payload     The payload of the aggregate, containing the actual domain object
 * @param aggregateId The unique identifier of the aggregate
 */
public record Aggregate(Long version,
                        Object payload,
                        String aggregateId) implements VersionSupplier, PayloadSupplier, AggregateIdSupplier, Comparable<Aggregate> {

    /**
     * Compact constructor for the Aggregate record.
     * Validates that the version is not null and is non-negative, and that the aggregateId is not empty.
     *
     * @throws BadArgumentException if version is null, version is negative, or aggregateId is empty
     */
    public Aggregate {
        if (isNull(version)) {
            throw new BadArgumentException("version cannot be null");
        } else if (version < 0) {
            throw new BadArgumentException("version must be greater than 0");
        }
        if (StringUtils.isBlank(aggregateId)) throw new BadArgumentException("aggregateId cannot be empty");
    }

    /**
     * Creates a new Aggregate with the specified aggregateId, version 0, and null payload.
     * This constructor is typically used when creating a new aggregate.
     *
     * @param aggregateId The unique identifier of the aggregate
     */
    public Aggregate(String aggregateId) {
        this(0L, null, aggregateId);
    }

    /**
     * Creates a new Aggregate with the specified version and payload.
     * The aggregateId is extracted from the payload.
     *
     * @param version The version of the aggregate
     * @param payload The payload of the aggregate
     */
    public Aggregate(Long version, Object payload) {
        this(version, payload, AggregateIdSupplier.getAggregateId(payload));
    }

    /**
     * Returns the snapshot threshold for this aggregate.
     * The threshold determines how many events should be processed before creating a snapshot.
     *
     * @return The snapshot threshold, or 0 if snapshots are not enabled or no threshold is specified
     */
    public int threshold() {
        return findAggregateAnnotationInPayload()
                .filter(io.github.cnadjim.eventflow.annotation.Aggregate::enableSnapshot)
                .filter(annotation -> annotation.threshold() > 0)
                .map(io.github.cnadjim.eventflow.annotation.Aggregate::threshold)
                .orElse(0);
    }

    /**
     * Checks if snapshots are enabled for this aggregate.
     *
     * @return true if snapshots are enabled, false otherwise
     */
    public boolean isSnapshotEnabled() {
        return findAggregateAnnotationInPayload()
                .map(io.github.cnadjim.eventflow.annotation.Aggregate::enableSnapshot)
                .orElse(false);
    }

    /**
     * Compares this aggregate with another aggregate based on their versions.
     * This allows aggregates to be sorted by version.
     *
     * @param aggregateWrapper The aggregate to compare with
     * @return A negative integer, zero, or a positive integer as this aggregate's version
     * is less than, equal to, or greater than the specified aggregate's version
     */
    @Override
    public int compareTo(Aggregate aggregateWrapper) {
        return version.compareTo(aggregateWrapper.version);
    }
}
