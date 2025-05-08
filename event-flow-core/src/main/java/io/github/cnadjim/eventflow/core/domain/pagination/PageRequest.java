package io.github.cnadjim.eventflow.core.domain.pagination;

public interface PageRequest {

    int pageNumber();

    int pageSize();

    SortDetails sort();


    static PageRequest unpaged() {
        return new DefaultPageRequest(0 ,0 , SortDetails.unSorted());
    }

    static PageRequest of(int pageNumber, int pageSize) {
        return new DefaultPageRequest(pageNumber, pageSize, SortDetails.unSorted());
    }

    static PageRequest of(int pageNumber, int pageSize, Order.Direction direction, String... fields) {
        return new DefaultPageRequest(pageNumber, pageSize, SortDetails.by(direction, fields));
    }
}
