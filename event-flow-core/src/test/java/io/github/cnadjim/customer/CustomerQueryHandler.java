package io.github.cnadjim.customer;

import io.github.cnadjim.eventflow.annotation.HandleQuery;

import java.util.Collection;
import java.util.Collections;

public class CustomerQueryHandler {

    @HandleQuery
    public Collection<CustomerEntity> handle(CustomerQuery.FindAllCustomer findAllCustomer) {
        return Collections.singletonList(new CustomerEntity());
    }

    @HandleQuery
    public CustomerEntity handle(CustomerQuery.FindCustomerById findCustomerById) {
        return new CustomerEntity(findCustomerById.id(), null, true);
    }

    @HandleQuery
    public CustomerEntity handle(CustomerQuery.ThrowAError throwAError) {
        throw new RuntimeException("error occurred");
    }
}
