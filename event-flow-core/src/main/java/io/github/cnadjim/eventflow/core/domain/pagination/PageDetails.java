package io.github.cnadjim.eventflow.core.domain.pagination;

/**
 * Interface representing pagination details for query results.
 * Provides information about the current page, page size, and total counts.
 */
public interface PageDetails {
    /**
     * Returns the current page number.
     *
     * @return The current page number (0-based)
     */
    long number();

    /**
     * Returns the size of the page (number of elements per page).
     *
     * @return The page size
     */
    long size();

    /**
     * Returns the total number of elements across all pages.
     *
     * @return The total number of elements
     */
    long totalElements();

    /**
     * Returns the total number of pages.
     *
     * @return The total number of pages
     */
    long totalPages();

    /**
     * Creates an unpaged PageDetails instance with all values set to 0.
     * This is useful when pagination is not applicable or not required.
     *
     * @return A PageDetails instance representing an unpaged result
     */
    static PageDetails unPaged(){
        return new DefaultPageDetails(0,0,0,0);
    }
}
