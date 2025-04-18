package io.github.cnadjim.eventflow.core.domain.pagination;

/**
 * Default implementation of the {@link PageDetails} interface.
 * This record provides pagination information for query results.
 *
 * @param number         The current page number (0-based)
 * @param size           The size of the page (number of elements per page)
 * @param totalElements  The total number of elements across all pages
 * @param totalPages     The total number of pages
 */
public record DefaultPageDetails(long number,
                                 long size,
                                 long totalElements,
                                 long totalPages) implements PageDetails {

}
