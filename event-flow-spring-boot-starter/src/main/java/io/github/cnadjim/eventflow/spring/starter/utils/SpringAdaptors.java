package io.github.cnadjim.eventflow.spring.starter.utils;

import io.github.cnadjim.eventflow.core.domain.pagination.Page;
import io.github.cnadjim.eventflow.core.domain.pagination.PageRequest;
import io.github.cnadjim.eventflow.spring.starter.adaptor.*;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Pageable;

import java.util.function.Function;

@UtilityClass
public class SpringAdaptors {

    private static final OrderAdaptor orderAdaptor = new OrderAdaptor();
    private static final PageDetailsAdaptor pageDetailsAdaptor = new PageDetailsAdaptor();
    private static final SortDetailsAdaptor sortDetailsAdaptor = new SortDetailsAdaptor(orderAdaptor);
    private static final PageRequestAdaptor pageRequestAdaptor = new PageRequestAdaptor(sortDetailsAdaptor);
    private static final io.github.cnadjim.eventflow.spring.starter.adaptor.PageAdaptor pageAdaptor = new PageAdaptor(sortDetailsAdaptor, pageDetailsAdaptor);


    public static <O, I> Page<O> toPage(org.springframework.data.domain.Page<I> springPage, Function<I, O> mapping) {
        return pageAdaptor.fromSpring(springPage, mapping);
    }

    public static <T> Page<T> toPage(org.springframework.data.domain.Page<T> springPage) {
        return pageAdaptor.fromSpring(springPage);
    }

    public static Pageable toPageable(PageRequest pageRequest) {
        return pageRequestAdaptor.toSpring(pageRequest);
    }

}
