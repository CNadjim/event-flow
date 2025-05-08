package io.github.cnadjim.eventflow.spring.starter.adaptor;

import io.github.cnadjim.eventflow.core.domain.pagination.DefaultOrder;
import io.github.cnadjim.eventflow.core.domain.pagination.Order;
import org.springframework.data.domain.Sort;

public class OrderAdaptor implements SpringAdaptor<Sort.Order, Order> {

    @Override
    public Sort.Order toSpring(Order order) {
        if (order == null) {
            return null;
        }

        final Sort.Direction direction;

        switch (order.direction()) {
            case ASC -> direction = Sort.Direction.ASC;
            case DESC -> direction = Sort.Direction.DESC;
            default -> throw new IllegalStateException("Unexpected value: " + order.direction());
        }

        return new Sort.Order(direction, order.field());
    }

    @Override
    public Order fromSpring(Sort.Order springOrder) {
        if (springOrder == null) {
            return null;
        }

        Order.Direction direction;
        switch (springOrder.getDirection()) {
            case ASC -> direction = Order.Direction.ASC;
            case DESC -> direction = Order.Direction.DESC;
            default -> throw new IllegalStateException("Unexpected value: " + springOrder.getDirection());
        }

        return new DefaultOrder(springOrder.getProperty(), direction);
    }
}
