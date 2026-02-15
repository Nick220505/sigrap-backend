package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.sigrap.sale.domain.model.Sale;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting domain Sale entities to SaleResponse DTOs.
 * This mapper is used by the REST adapter to translate domain objects to HTTP responses.
 */
@Component
public class SaleResponseMapper {
    
    /**
     * Converts a domain Sale entity to a SaleResponse DTO.
     *
     * @param sale the domain sale entity
     * @return the sale response DTO
     */
    public SaleResponse toResponse(Sale sale) {
        if (sale == null) {
            return null;
        }
        
        return new SaleResponse(
            sale.getId() != null ? sale.getId().value() : null,
            sale.getSaleNumber().value(),
            sale.getCustomerId(),
            sale.getEmployeeId(),
            sale.getSaleDate(),
            sale.getTotalAmount(),
            sale.getPaymentMethod(),
            sale.getStatus(),
            sale.getNotes(),
            sale.getCreatedAt(),
            sale.getUpdatedAt()
        );
    }
}
