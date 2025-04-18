package io.github.cnadjim.eventflow.core.domain.pagination;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;

import java.util.Collection;

import static java.util.Objects.isNull;

/**
 * Default implementation of the {@link Page} interface.
 * This record represents a paginated result set with content, pagination details, and sorting information.
 *
 * @param page           The pagination details for this page
 * @param sort           The sorting details for this page
 * @param content        The collection of elements in this page
 * @param <CONTENT_TYPE> The type of elements in the page
 */
public record DefaultPage<CONTENT_TYPE>(PageDetails page,
                                        SortDetails sort,
                                        Collection<CONTENT_TYPE> content) implements Page<CONTENT_TYPE> {
    public DefaultPage {
        if (isNull(page)) throw new BadArgumentException("page cannot be null");
        if (isNull(sort)) throw new BadArgumentException("sort cannot be null");
        if (isNull(content)) throw new BadArgumentException("content cannot be null");
    }
}
