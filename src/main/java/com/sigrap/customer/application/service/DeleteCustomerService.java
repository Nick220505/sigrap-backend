package com.sigrap.customer.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.BulkEntityDeletedEvent;
import com.sigrap.audit.domain.event.EntityDeletedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.customer.application.port.in.DeleteCustomerUseCase;
import com.sigrap.customer.domain.model.CustomerId;
import com.sigrap.customer.domain.port.CustomerRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DeleteCustomerService implements DeleteCustomerUseCase {
    
    private final CustomerRepositoryPort customerRepository;
    private final EventPublisherPort eventPublisher;
    
    public DeleteCustomerService(CustomerRepositoryPort customerRepository, EventPublisherPort eventPublisher) {
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void delete(CustomerId id) {
        long startTime = System.currentTimeMillis();
        
        if (!customerRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException(
                "Customer with ID '" + id.value() + "' not found"
            );
        }
        
        customerRepository.deleteById(id);
        
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityDeletedEvent(
            EntityType.CUSTOMER,
            id.value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            "Customer deleted",
            durationMs
        ));
    }
    
    @Override
    public void deleteAll(List<CustomerId> ids) {
        if (ids == null || ids.isEmpty()) {
            customerRepository.deleteAllById(List.of());
            return;
        }
        
        long startTime = System.currentTimeMillis();
        
        for (CustomerId id : ids) {
            if (!customerRepository.findById(id).isPresent()) {
                throw new ResourceNotFoundException(
                    "Customer with ID '" + id.value() + "' not found"
                );
            }
        }
        
        customerRepository.deleteAllById(ids);
        
        long durationMs = System.currentTimeMillis() - startTime;
        List<String> entityIds = ids.stream()
            .map(id -> id.value().toString())
            .toList();
        eventPublisher.publish(new BulkEntityDeletedEvent(
            EntityType.CUSTOMER,
            entityIds,
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            durationMs
        ));
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
