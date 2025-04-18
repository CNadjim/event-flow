package io.github.cnadjim.eventflow.core.domain.pagination;

/**
 * Interface representing a sort order criterion.
 * Each Order instance specifies a field to sort by and a direction (ascending or descending).
 */
public interface Order {

    /**
     * Enumeration of possible sort directions.
     */
    enum Direction {
        /**
         * Ascending order (A to Z, 0 to 9)
         */
        ASC,

        /**
         * Descending order (Z to A, 9 to 0)
         */
        DESC
    }

    /**
     * Returns the field name to sort by.
     *
     * @return The name of the field
     */
    String field();

    /**
     * Returns the sort direction.
     *
     * @return The direction (ASC or DESC)
     */
    Direction direction();
}
