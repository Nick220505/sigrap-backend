package com.sigrap.supplier.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityCreatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.supplier.application.port.in.CreateSupplierUseCase;
import com.sigrap.supplier.application.port.in.command.CreateSupplierCommand;
import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierEmail;
import com.sigrap.supplier.domain.model.SupplierName;
import com.sigrap.supplier.domain.model.SupplierPhone;
import com.sigrap.supplier.domain.port.SupplierRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CreateSupplierService implements CreateSupplierUseCase {
    
    private final SupplierRepositoryPort supplierRepository;
    private final EventPublisherPort eventPublisher;
    
    public CreateSupplierService(SupplierRepositoryPort supplierRepository, EventPublisherPort eventPublisher) {
        this.supplierRepository = supplierRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Supplier create(CreateSupplierCommand command) {
        long startTime = System.currentTimeMillis();
        
        SupplierName name = new SupplierName(command.name());
        SupplierEmail email = new SupplierEmail(command.email());
        SupplierPhone phone = command.phone() != null && !command.phone().isBlank() 
            ? new SupplierPhone(command.phone()) 
            : null;
        
        if (supplierRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                "Supplier with email '" + email.value() + "' already exists"
            );
        }
        
        Supplier supplier = new Supplier(
            name,
            command.contactName(),
            email,
            phone,
            command.address()
        );
        
        Supplier savedSupplier = supplierRepository.save(supplier);
        
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityCreatedEvent(
            EntityType.SUPPLIER,
            savedSupplier.getId().value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            "Supplier created: " + savedSupplier.getName().value(),
            durationMs
        ));
        
        return savedSupplier;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
