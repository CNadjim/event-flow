package io.github.cnadjim.evenflow.core.domain;


import io.github.cnadjim.eventflow.annotation.AggregateId;
import io.github.cnadjim.eventflow.annotation.Topic;

@Topic("commands.customer")
public interface CustomerCommand  {

    record CreateCustomerCommand(@AggregateId String id, String name) implements CustomerCommand {
    }

    record UpdateCustomerNameCommand(@AggregateId String id, String newName) implements CustomerCommand {
    }
}
