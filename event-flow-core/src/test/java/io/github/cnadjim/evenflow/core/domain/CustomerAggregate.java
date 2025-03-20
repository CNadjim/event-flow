package io.github.cnadjim.evenflow.core.domain;

import io.github.cnadjim.evenflow.core.domain.CustomerEvent.CustomerBirthdayUpdatedEvent;
import io.github.cnadjim.eventflow.annotation.Aggregate;
import io.github.cnadjim.eventflow.annotation.AggregateId;
import io.github.cnadjim.eventflow.annotation.ApplyEvent;
import io.github.cnadjim.eventflow.annotation.HandleCommand;
import io.github.cnadjim.eventflow.core.domain.exception.EventFlowIllegalArgumentException;

import java.time.LocalDate;

import static io.github.cnadjim.evenflow.core.domain.CustomerCommand.CreateCustomerCommand;
import static io.github.cnadjim.evenflow.core.domain.CustomerCommand.UpdateCustomerNameCommand;
import static io.github.cnadjim.evenflow.core.domain.CustomerEvent.CustomerCreatedEvent;
import static io.github.cnadjim.evenflow.core.domain.CustomerEvent.CustomerNameUpdatedEvent;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Aggregate(enableSnapshot = true, threshold = 200)
public class CustomerAggregate {

    @AggregateId
    private String id;
    private String name;
    private Boolean enabled;
    private LocalDate birthday;

    @HandleCommand
    public CustomerEvent handle(CreateCustomerCommand createCustomerCommand) {
        return new CustomerCreatedEvent(createCustomerCommand.id(), createCustomerCommand.name());
    }

    @HandleCommand
    public CustomerEvent handle(UpdateCustomerNameCommand updateCustomerNameCommand) {
        return new CustomerNameUpdatedEvent(updateCustomerNameCommand.id(), updateCustomerNameCommand.newName());
    }


    @HandleCommand
    public CustomerEvent handle(CustomerCommand.UpdateCustomerBirthdayCommand updateCustomerBirthdayCommand) {
        return new CustomerBirthdayUpdatedEvent(updateCustomerBirthdayCommand.id(), updateCustomerBirthdayCommand.newBirthDay());
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

    @ApplyEvent
    public CustomerAggregate apply(CustomerBirthdayUpdatedEvent event, CustomerAggregate aggregate) {
        if (isNull(aggregate)) {
            throw new EventFlowIllegalArgumentException("Customer does not exist for id " + event.id());
        }
        return CustomerAggregateBuilder.from(aggregate)
                .birthday(event.newBirthDay())
                .build();
    }
}
