package com.sigrap.sale.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;
import com.sigrap.audit.domain.event.EntityCreatedEvent;
import com.sigrap.audit.domain.model.EntityType;
import com.sigrap.sale.application.port.in.CreateSaleUseCase;
import com.sigrap.sale.application.port.in.command.CreateSaleCommand;
import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleNumber;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CreateSaleService implements CreateSaleUseCase {
    
    private final SaleRepositoryPort saleRepository;
    private final EventPublisherPort eventPublisher;
    
    public CreateSaleService(SaleRepositoryPort saleRepository, EventPublisherPort eventPublisher) {
        this.saleRepository = saleRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Sale create(CreateSaleCommand command) {
        long startTime = System.currentTimeMillis();
        
        SaleNumber saleNumber = new SaleNumber(command.saleNumber());
        
        Sale sale = new Sale(
            saleNumber,
            command.customerId(),
            command.employeeId(),
            command.saleDate(),
            command.paymentMethod(),
            command.notes()
        );
        
        Sale savedSale = saleRepository.save(sale);
        
        long durationMs = System.currentTimeMillis() - startTime;
        eventPublisher.publish(new EntityCreatedEvent(
            EntityType.SALE,
            savedSale.getId().value().toString(),
            getCurrentUsername(),
            LocalDateTime.now(),
            null,
            null,
            "Sale created: " + savedSale.getSaleNumber().value(),
            durationMs
        ));
        
        return savedSale;
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "system";
    }
}
