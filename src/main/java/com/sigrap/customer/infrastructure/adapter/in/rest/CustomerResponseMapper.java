package com.sigrap.customer.infrastructure.adapter.in.rest;

import com.sigrap.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting domain Customer entities to CustomerResponse DTOs.
 * This mapper is used by the REST adapter to translate domain objects to HTTP responses.
 */
@Component
public class CustomerResponseMapper {
    
    /**
     * Converts a domain Customer entity to a CustomerResponse DTO.
     *
     * @param customer the domain customer entity
     * @return the customer response DTO
     */
    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        return new CustomerResponse(
            customer.getId() != null ? customer.getId().value() : null,
            customer.getFullName().value(),
            customer.getDocumentId(),
            customer.getEmail().value(),
            customer.getPhoneNumber() != null ? customer.getPhoneNumber().value() : null,
            customer.getAddress(),
            customer.getCreatedAt(),
            customer.getUpdatedAt()
        );
    }
}
