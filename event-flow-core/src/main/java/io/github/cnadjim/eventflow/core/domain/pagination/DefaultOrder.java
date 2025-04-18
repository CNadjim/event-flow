package io.github.cnadjim.eventflow.core.domain.pagination;

/**
 * Default implementation of the {@link Order} interface.
 * This record represents a sort order criterion with a field name and a direction.
 *
 * @param field      The name of the field to sort by
 * @param direction  The sort direction (ASC or DESC)
 */
public record DefaultOrder(String field, Direction direction) implements Order {
}
