package io.github.cnadjim.eventflow.core.domain.pagination;

import java.util.Collection;
import java.util.Collections;

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
}
