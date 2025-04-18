package io.github.cnadjim.eventflow.spring.starter.pagination;


import io.github.cnadjim.eventflow.core.domain.pagination.*;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public interface PageAdaptor {

    static <T> Page<T> ofSpringPage(org.springframework.data.domain.Page<T> springPage) {
        if (springPage == null) {
            return Page.empty();
        }

        final PageDetails pageDetails = new DefaultPageDetails(
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages()
        );

        final SortDetails sortDetails = convertSortDetails(springPage.getSort());

        return new DefaultPage<>(pageDetails, sortDetails, springPage.getContent());
    }

    static SortDetails convertSortDetails(Sort sort) {
        if (sort == null || sort.isEmpty()) {
            return SortDetails.unSorted();
        }

        final List<Order> orders = new ArrayList<>();

        for (Sort.Order springOrder : sort) {
            orders.add(new DefaultOrder(springOrder.getProperty(), convertDirection(springOrder.getDirection())));
        }

        return new DefaultSortDetails(orders);
    }

    static Order.Direction convertDirection(Sort.Direction direction){
        switch (direction){
            case ASC -> {
                return Order.Direction.ASC;
            }
            case DESC -> {
                return Order.Direction.DESC;
            }
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        }
    }

}
