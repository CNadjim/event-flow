package io.github.cnadjim.eventflow.core.domain.pagination;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * Interface representing sorting details for query results.
 * Provides a collection of {@link Order} objects that define the sorting criteria.
 */
public interface SortDetails {
    /**
     * Returns the collection of sort orders to be applied.
     * Each order specifies a field and a direction (ascending or descending).
     *
     * @return A collection of Order objects
     */
    Collection<Order> orders();

    /**
     * Creates an unsorted SortDetails instance with an empty collection of orders.
     * This is useful when sorting is not required or not applicable.
     *
     * @return A SortDetails instance representing an unsorted result
     */
    static SortDetails unSorted() {
        return new DefaultSortDetails(Collections.emptyList());
    }

    static SortDetails by(List<Order> orders) {
        if (isNull(orders)) throw new BadArgumentException("Orders must not be null");
        return new DefaultSortDetails(orders);
    }

    static SortDetails by(Order.Direction direction, String... fields) {
        if (isNull(direction)) throw new BadArgumentException("Direction must not be null");
        if (isNull(fields)) throw new BadArgumentException("Properties must not be null");
        if (fields.length < 1) throw new BadArgumentException("At least one field must be given");
        final List<Order> orders = Arrays.stream(fields)
                .map(field -> new DefaultOrder(field, direction))
                .collect(Collectors.toList());
        return SortDetails.by(orders);
    }
}
