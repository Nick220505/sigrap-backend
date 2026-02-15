package com.sigrap.supplier.infrastructure.adapter.in.rest;

import com.sigrap.supplier.domain.model.Supplier;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting domain Supplier entities to SupplierResponse DTOs.
 * This mapper is used by the REST adapter to translate domain objects to HTTP responses.
 */
@Component
public class SupplierResponseMapper {
    
    /**
     * Converts a domain Supplier entity to a SupplierResponse DTO.
     *
     * @param supplier the domain supplier entity
     * @return the supplier response DTO
     */
    public SupplierResponse toResponse(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        
        return new SupplierResponse(
            supplier.getId() != null ? supplier.getId().value() : null,
            supplier.getName().value(),
            supplier.getContactName(),
            supplier.getEmail().value(),
            supplier.getPhone() != null ? supplier.getPhone().value() : null,
            supplier.getAddress(),
            supplier.getCreatedAt(),
            supplier.getUpdatedAt()
        );
    }
}
