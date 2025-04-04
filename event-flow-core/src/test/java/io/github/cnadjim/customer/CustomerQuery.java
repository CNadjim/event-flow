package io.github.cnadjim.customer;

public interface CustomerQuery {
    record FindAllCustomer(){

    };

    record FindCustomerById(String id){

    }

    record ThrowAError(){

    }
}
