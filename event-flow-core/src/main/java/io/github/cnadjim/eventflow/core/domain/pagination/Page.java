package io.github.cnadjim.eventflow.core.domain.pagination;

import java.util.Collection;
import java.util.Collections;

/**
 * Interface representing a paginated result set.
 * Provides access to the content of the page, pagination details, and sorting information.
 *
 * @param <CONTENT_TYPE> The type of elements in the page
 */
public interface Page<CONTENT_TYPE> {

    /**
     * Returns the content of the page as a collection of elements.
     *
     * @return A collection of elements of type CONTENT_TYPE
     */
    Collection<CONTENT_TYPE> content();

    /**
     * Returns the pagination details for this page.
     *
     * @return The PageDetails object containing pagination information
     */
    PageDetails page();

    /**
     * Returns the sorting details for this page.
     *
     * @return The SortDetails object containing sorting information
     */
    SortDetails sort();

    /**
     * Creates an empty page with no content, unpaged pagination details, and unsorted sorting details.
     * This is useful when no results are available or when pagination is not applicable.
     *
     * @param <CONTENT_TYPE> The type of elements that would be in the page
     * @return An empty Page instance
     */
    static <CONTENT_TYPE> Page<CONTENT_TYPE> empty() {
        return new DefaultPage<>(PageDetails.unPaged(), SortDetails.unSorted(), Collections.emptyList());
    }
}
