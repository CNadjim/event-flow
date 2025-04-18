package io.github.cnadjim.eventflow.core.domain.response;


import io.github.cnadjim.eventflow.core.domain.pagination.DefaultPage;
import io.github.cnadjim.eventflow.core.domain.pagination.Page;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * Implementation of {@link ResponseType} for paginated responses.
 * This record converts response objects to {@link Page} instances containing elements of the specified type.
 *
 * @param responseType The class of elements to include in the page
 * @param <R> The type of elements in the page
 */
public record PageResponseType<R>(Class<R> responseType) implements ResponseType<Page<R>> {

    /**
     * Converts a response object to a Page containing elements of type R.
     * If the response is null, returns an empty page.
     * If the response is already a Page, filters its content to include only elements of type R.
     *
     * @param response The response object to convert
     * @return A Page containing elements of type R
     * @throws IllegalArgumentException if the response cannot be converted to a Page of R
     */
    @Override
    public Page<R> convert(Object response) {
        if (isNull(response)) {
            return Page.empty();
        }

        if (response instanceof Page<?> pageResponse) {
            final List<R> filteredContent = pageResponse.content().stream()
                    .filter(item -> responseType.isAssignableFrom(item.getClass()))
                    .map(responseType::cast)
                    .collect(Collectors.toList());

            return new DefaultPage<>(pageResponse.page(), pageResponse.sort(), filteredContent);
        }

        throw new IllegalArgumentException("Cannot convert response to a paginated response of " + responseType.getName());
    }
}
