package io.github.cnadjim.evenflow.core.domain;

import io.github.cnadjim.eventflow.core.domain.annotation.Aggregate;
import io.github.cnadjim.eventflow.core.domain.annotation.AggregateId;
import io.github.cnadjim.eventflow.core.domain.annotation.ApplyEvent;
import io.github.cnadjim.eventflow.core.domain.annotation.HandleCommand;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowIllegalArgumentException;

import static io.github.cnadjim.evenflow.core.domain.CustomerCommand.CreateCustomerCommand;
import static io.github.cnadjim.evenflow.core.domain.CustomerCommand.UpdateCustomerNameCommand;
import static io.github.cnadjim.evenflow.core.domain.CustomerEvent.CustomerCreatedEvent;
import static io.github.cnadjim.evenflow.core.domain.CustomerEvent.CustomerNameUpdatedEvent;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Aggregate
public class CustomerAggregate {

    @AggregateId
    private String id;
    private String name;

    @HandleCommand
    public CustomerEvent handle(CreateCustomerCommand createCustomerCommand) {
        return new CustomerCreatedEvent(createCustomerCommand.id(), createCustomerCommand.name());
    }

    @HandleCommand
    public CustomerEvent handle(UpdateCustomerNameCommand updateCustomerNameCommand) {
        return new CustomerNameUpdatedEvent(updateCustomerNameCommand.id(), updateCustomerNameCommand.newName());
    }

    @ApplyEvent
    public CustomerAggregate apply(CustomerCreatedEvent event, CustomerAggregate aggregate) {
        if (nonNull(aggregate)) {
            throw new EventFlowIllegalArgumentException("Customer already exists for id " + event.id());
        }

        return CustomerAggregateBuilder.builder()
                .id(event.id())
                .name(event.name())
                .build();
    }

    @ApplyEvent
    public CustomerAggregate apply(CustomerNameUpdatedEvent event, CustomerAggregate aggregate) {
        if (isNull(aggregate)) {
            throw new EventFlowIllegalArgumentException("Customer does not exist for id " + event.id());
        }

        return CustomerAggregateBuilder.from(aggregate)
                .name(event.newName())
                .build();
    }
}
