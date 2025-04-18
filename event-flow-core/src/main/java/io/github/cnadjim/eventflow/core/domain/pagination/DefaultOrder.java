package io.github.cnadjim.eventflow.core.domain.pagination;

import io.github.cnadjim.eventflow.core.domain.exception.BadArgumentException;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.isNull;

/**
 * Default implementation of the {@link Order} interface.
 * This record represents a sort order criterion with a field name and a direction.
 *
 * @param field     The name of the field to sort by
 * @param direction The sort direction (ASC or DESC)
 */
public record DefaultOrder(String field, Direction direction) implements Order {

    public DefaultOrder {
        if (StringUtils.isBlank(field)) throw new BadArgumentException("field cannot be blank");
        if (isNull(direction)) throw new BadArgumentException("direction cannot be null");
    }
}
