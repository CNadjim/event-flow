package io.github.cnadjim.customer;

import io.github.cnadjim.customer.CustomerEvent.CustomerBirthdayUpdatedEvent;
import io.github.cnadjim.eventflow.annotation.Aggregate;
import io.github.cnadjim.eventflow.annotation.AggregateId;
import io.github.cnadjim.eventflow.annotation.ApplyEvent;
import io.github.cnadjim.eventflow.annotation.HandleCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static io.github.cnadjim.customer.CustomerCommand.CreateCustomerCommand;
import static io.github.cnadjim.customer.CustomerCommand.UpdateCustomerNameCommand;
import static io.github.cnadjim.customer.CustomerEvent.CustomerCreatedEvent;
import static io.github.cnadjim.customer.CustomerEvent.CustomerNameUpdatedEvent;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Aggregate(threshold = 50)
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
            throw new RuntimeException("Customer already exists for id " + event.id());
        }

        return CustomerAggregate.builder()
                .id(event.id())
                .name(event.name())
                .build();
    }

    @ApplyEvent
    public CustomerAggregate apply(CustomerNameUpdatedEvent event, CustomerAggregate aggregate) {
        if (isNull(aggregate)) {
            throw new RuntimeException("Customer does not exist for id " + event.id());
        }

        return aggregate.toBuilder()
                .name(event.newName())
                .build();
    }

    @ApplyEvent
    public CustomerAggregate apply(CustomerBirthdayUpdatedEvent event, CustomerAggregate aggregate) {
        if (isNull(aggregate)) {
            throw new RuntimeException("Customer does not exist for id " + event.id());
        }
        return aggregate.toBuilder()
                .birthday(event.newBirthDay())
                .build();
    }
}
