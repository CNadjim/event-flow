package io.github.cnadjim.eventflow.core.domain.pagination;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;

import java.util.Collection;

import static java.util.Objects.isNull;

/**
 * Default implementation of the {@link SortDetails} interface.
 * This record provides sorting information for query results.
 *
 * @param orders A collection of {@link Order} objects that define the sorting criteria
 */
public record DefaultSortDetails(Collection<Order> orders) implements SortDetails {

    public DefaultSortDetails {
        if (isNull(orders)) throw new BadArgumentException("orders cannot be null");
    }
}
