package io.github.cnadjim.eventflow.core.domain.pagination;

import java.util.Collection;

/**
 * Default implementation of the {@link SortDetails} interface.
 * This record provides sorting information for query results.
 *
 * @param orders A collection of {@link Order} objects that define the sorting criteria
 */
public record DefaultSortDetails(Collection<Order> orders) implements SortDetails {
}
