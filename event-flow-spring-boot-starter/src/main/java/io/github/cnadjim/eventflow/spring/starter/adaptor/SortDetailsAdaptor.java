package io.github.cnadjim.eventflow.spring.starter.adaptor;

import io.github.cnadjim.eventflow.core.domain.pagination.DefaultSortDetails;
import io.github.cnadjim.eventflow.core.domain.pagination.Order;
import io.github.cnadjim.eventflow.core.domain.pagination.SortDetails;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SortDetailsAdaptor implements SpringAdaptor<Sort, SortDetails> {

    private final OrderAdaptor orderAdaptor;

    public SortDetailsAdaptor(OrderAdaptor orderAdaptor) {
        this.orderAdaptor = orderAdaptor;
    }

    @Override
    public Sort toSpring(SortDetails sortDetails) {
        if (sortDetails == null || sortDetails.orders().isEmpty()) {
            return Sort.unsorted();
        }

        final List<Sort.Order> orders = sortDetails.orders().stream()
                .map(orderAdaptor::toSpring)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return Sort.by(orders);
    }

    @Override
    public SortDetails fromSpring(Sort sort) {
        if (sort == null || sort.isEmpty()) {
            return SortDetails.unSorted();
        }

        List<Order> orders = new ArrayList<>();
        for (Sort.Order springOrder : sort) {
            orders.add(orderAdaptor.fromSpring(springOrder));
        }

        return new DefaultSortDetails(orders);
    }
}
