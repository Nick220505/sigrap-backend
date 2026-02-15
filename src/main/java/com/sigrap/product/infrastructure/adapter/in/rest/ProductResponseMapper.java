package com.sigrap.product.infrastructure.adapter.in.rest;

import com.sigrap.product.domain.model.Product;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting domain Product entities to ProductResponse DTOs.
 * This mapper is used by the REST adapter to translate domain objects to HTTP responses.
 */
@Component
public class ProductResponseMapper {
    
    /**
     * Converts a domain Product entity to a ProductResponse DTO.
     *
     * @param product the domain product entity
     * @return the product response DTO
     */
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        
        return new ProductResponse(
            product.getId() != null ? product.getId().value() : null,
            product.getName().value(),
            product.getDescription(),
            product.getCostPrice().value(),
            product.getSalePrice().value(),
            product.getStock().value(),
            product.getMinimumStockThreshold().value(),
            product.getCategoryId() != null ? product.getCategoryId().value() : null,
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}
