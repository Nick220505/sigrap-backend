package com.sigrap.customer.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityCreatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.customer.application.port.in.CreateCustomerUseCase;
import com.sigrap.customer.application.port.in.command.CreateCustomerCommand;
import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerEmail;
import com.sigrap.customer.domain.model.CustomerName;
import com.sigrap.customer.domain.model.CustomerPhone;
import com.sigrap.customer.domain.port.CustomerRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementing the CreateCustomerUseCase.
 * This service orchestrates the creation of a new customer by:
 * <ul>
 *   <li>Validating business rules (e.g., unique customer email)</li>
 *   <li>Creating the domain entity</li>
 *   <li>Persisting through the repository port</li>
 * </ul>
 * 
 * <p>Following hexagonal architecture principles:
 * <ul>
 *   <li>Implements input port interface</li>
 *   <li>Uses output port (repository) for persistence</li>
 *   <li>Manages transactions at use case level</li>
 *   <li>Contains no infrastructure concerns</li>
 * </ul>
 */
@Service
@Transactional
public class CreateCustomerService implements CreateCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final EventPublisherPort eventPublisher;

    public CreateCustomerService(CustomerRepositoryPort customerRepository, EventPublisherPort eventPublisher) {
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Customer create(CreateCustomerCommand command) {
        long startTime = System.currentTimeMillis();

        CustomerName fullName = new CustomerName(command.fullName());
        CustomerEmail email = new CustomerEmail(command.email());
        CustomerPhone phoneNumber = command.phoneNumber() != null && !command.phoneNumber().isBlank()
            ? new CustomerPhone(command.phoneNumber())
            : null;

        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "Customer with email '" + email.value() + "' already exists"
            );
        }

        Customer customer = new Customer(
            fullName,
            command.documentId(),
            email,
            phoneNumber,
            command.address()
        );

        Customer savedCustomer = customerRepository.save(customer);

        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityCreatedEvent(
            EntityType.CUSTOMER,
            savedCustomer.getId().value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            "Customer created: " + savedCustomer.getFullName().value(),
            durationMs
        ));

        return savedCustomer;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}

