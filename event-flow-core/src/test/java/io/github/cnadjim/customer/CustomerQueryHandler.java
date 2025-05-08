package io.github.cnadjim.customer;

import io.github.cnadjim.eventflow.annotation.QueryHandler;

import java.util.Collection;
import java.util.Collections;

public class CustomerQueryHandler {

    @QueryHandler
    public Collection<CustomerEntity> handle(CustomerQuery.FindAllCustomer findAllCustomer) {
        return Collections.singletonList(new CustomerEntity());
    }

    @QueryHandler
    public CustomerEntity handle(CustomerQuery.FindCustomerById findCustomerById) {
        return new CustomerEntity(findCustomerById.id(), null, true);
    }

    @QueryHandler
    public CustomerEntity handle(CustomerQuery.ThrowAError throwAError) {
        throw new RuntimeException("error occurred");
    }
}
