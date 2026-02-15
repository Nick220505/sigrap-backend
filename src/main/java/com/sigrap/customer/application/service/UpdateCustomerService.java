package com.sigrap.customer.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityUpdatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.customer.application.port.in.UpdateCustomerUseCase;
import com.sigrap.customer.application.port.in.command.UpdateCustomerCommand;
import com.sigrap.customer.domain.model.Customer;
import com.sigrap.customer.domain.model.CustomerEmail;
import com.sigrap.customer.domain.model.CustomerId;
import com.sigrap.customer.domain.model.CustomerName;
import com.sigrap.customer.domain.model.CustomerPhone;
import com.sigrap.customer.domain.port.CustomerRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class UpdateCustomerService implements UpdateCustomerUseCase {
    
    private final CustomerRepositoryPort customerRepository;
    private final EventPublisherPort eventPublisher;
    
    public UpdateCustomerService(CustomerRepositoryPort customerRepository, EventPublisherPort eventPublisher) {
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Customer update(CustomerId id, UpdateCustomerCommand command) {
        long startTime = System.currentTimeMillis();
        
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Customer with ID '" + id.value() + "' not found"
            ));
        
        CustomerName newFullName = new CustomerName(command.fullName());
        CustomerEmail newEmail = new CustomerEmail(command.email());
        CustomerPhone newPhoneNumber = command.phoneNumber() != null && !command.phoneNumber().isBlank()
            ? new CustomerPhone(command.phoneNumber())
            : null;
        
        if (!customer.getEmail().equals(newEmail)) {
            if (customerRepository.existsByEmailAndIdNot(newEmail, id)) {
                throw new IllegalArgumentException(
                    "Customer with email '" + newEmail.value() + "' already exists"
                );
            }
            customer.updateEmail(newEmail);
        }
        
        if (!customer.getFullName().equals(newFullName)) {
            customer.updateFullName(newFullName);
        }
        
        customer.updateDocumentId(command.documentId());
        customer.updatePhoneNumber(newPhoneNumber);
        customer.updateAddress(command.address());
        
        Customer updatedCustomer = customerRepository.save(customer);
        
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityUpdatedEvent(
            EntityType.CUSTOMER,
            updatedCustomer.getId().value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            "Customer updated",
            durationMs
        ));
        
        return updatedCustomer;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
