package io.github.cnadjim.customer;

import io.github.cnadjim.customer.CustomerEvent.CustomerBirthdayUpdatedEvent;
import io.github.cnadjim.eventflow.annotation.Aggregate;
import io.github.cnadjim.eventflow.annotation.AggregateIdentifier;
import io.github.cnadjim.eventflow.annotation.EventSourcingHandler;
import io.github.cnadjim.eventflow.annotation.CommandHandler;
import io.github.cnadjim.eventflow.core.domain.exception.ConflictException;
import io.github.cnadjim.eventflow.core.domain.exception.ResourceNotFoundException;
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

    @AggregateIdentifier
    private String id;
    private String name;
    private Boolean enabled;
    private LocalDate birthday;

    @CommandHandler
    public CustomerEvent handle(CreateCustomerCommand createCustomerCommand) {
        return new CustomerCreatedEvent(createCustomerCommand.id(), createCustomerCommand.name());
    }

    @CommandHandler
    public CustomerEvent handle(UpdateCustomerNameCommand updateCustomerNameCommand) {
        return new CustomerNameUpdatedEvent(updateCustomerNameCommand.id(), updateCustomerNameCommand.newName());
    }


    @CommandHandler
    public CustomerEvent handle(CustomerCommand.UpdateCustomerBirthdayCommand updateCustomerBirthdayCommand) {
        return new CustomerBirthdayUpdatedEvent(updateCustomerBirthdayCommand.id(), updateCustomerBirthdayCommand.newBirthDay());
    }

    @EventSourcingHandler
    public CustomerAggregate apply(CustomerCreatedEvent event, CustomerAggregate aggregate) {
        if (nonNull(aggregate)) {
            throw new ConflictException("Customer already exists for id " + event.id());
        }

        return CustomerAggregate.builder()
                .id(event.id())
                .name(event.name())
                .build();
    }

    @EventSourcingHandler
    public CustomerAggregate apply(CustomerNameUpdatedEvent event, CustomerAggregate aggregate) {
        if (isNull(aggregate)) {
            throw new ResourceNotFoundException("Customer does not exist for id " + event.id());
        }

        return aggregate.toBuilder()
                .name(event.newName())
                .build();
    }

    @EventSourcingHandler
    public CustomerAggregate apply(CustomerBirthdayUpdatedEvent event, CustomerAggregate aggregate) {
        if (isNull(aggregate)) {
            throw new ResourceNotFoundException("Customer does not exist for id " + event.id());
        }
        return aggregate.toBuilder()
                .birthday(event.newBirthDay())
                .build();
    }
}
