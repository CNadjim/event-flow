package io.github.cnadjim.customer;

import io.github.cnadjim.customer.CustomerEvent.CustomerBirthdayUpdatedEvent;
import io.github.cnadjim.customer.CustomerEvent.CustomerDeletedEvent;
import io.github.cnadjim.eventflow.annotation.Aggregate;
import io.github.cnadjim.eventflow.annotation.AggregateIdentifier;
import io.github.cnadjim.eventflow.annotation.EventSourcingHandler;
import io.github.cnadjim.eventflow.annotation.CommandHandler;
import io.github.cnadjim.eventflow.core.domain.aggregate.AggregateLifecycle;
import io.github.cnadjim.eventflow.core.domain.exception.ConflictException;
import io.github.cnadjim.eventflow.core.domain.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

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
    public CustomerEvent handle(CustomerCommand.CreateCustomerCommand createCustomerCommand) {
        if (StringUtils.isNotBlank(id)) {
            throw new ConflictException("Customer already exists for id " + createCustomerCommand.id());
        }

        return new CustomerEvent.CustomerCreatedEvent(createCustomerCommand.id(), createCustomerCommand.name());
    }

    @CommandHandler
    public CustomerEvent handle(CustomerCommand.UpdateCustomerNameCommand updateCustomerNameCommand) {
        if (StringUtils.isBlank(id)) {
            throw new ResourceNotFoundException("Customer does not exist for id " + updateCustomerNameCommand.id());
        }

        return new CustomerEvent.CustomerNameUpdatedEvent(updateCustomerNameCommand.id(), updateCustomerNameCommand.newName());
    }

    @CommandHandler
    public CustomerEvent handle(CustomerCommand.UpdateCustomerBirthdayCommand updateCustomerBirthdayCommand) {
        if (StringUtils.isBlank(id)) {
            throw new ResourceNotFoundException("Customer does not exist for id " + updateCustomerBirthdayCommand.id());
        }

        return new CustomerEvent.CustomerBirthdayUpdatedEvent(updateCustomerBirthdayCommand.id(), updateCustomerBirthdayCommand.newBirthDay());
    }

    @EventSourcingHandler
    public void apply(CustomerCreatedEvent event) {
        id = event.id();
        name = event.name();
    }

    @EventSourcingHandler
    public void apply(CustomerNameUpdatedEvent event) {
        name = event.newName();
    }

    @EventSourcingHandler
    public void apply(CustomerBirthdayUpdatedEvent event) {
        birthday = event.newBirthDay();
    }

    @EventSourcingHandler
    public void apply(CustomerDeletedEvent event) {
        AggregateLifecycle.markDeleted();
    }
}
