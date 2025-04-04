package io.github.cnadjim.customer;


import io.github.cnadjim.eventflow.annotation.AggregateId;

import java.time.LocalDate;

public interface CustomerCommand  {

    record CreateCustomerCommand(@AggregateId String id, String name) implements CustomerCommand {
    }

    record UpdateCustomerNameCommand(@AggregateId String id, String newName) implements CustomerCommand {
    }

    record UpdateCustomerBirthdayCommand(@AggregateId String id, LocalDate newBirthDay) implements CustomerCommand {
    }
}
