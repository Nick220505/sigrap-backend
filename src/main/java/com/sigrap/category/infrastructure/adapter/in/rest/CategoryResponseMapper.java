package com.sigrap.category.infrastructure.adapter.in.rest;

import com.sigrap.category.domain.model.Category;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting domain Category entities to CategoryResponse DTOs.
 * This mapper is used by the REST adapter to translate domain objects to HTTP responses.
 */
@Component
public class CategoryResponseMapper {
    
    /**
     * Converts a domain Category entity to a CategoryResponse DTO.
     *
     * @param category the domain category entity
     * @return the category response DTO
     */
    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        return new CategoryResponse(
            category.getId() != null ? category.getId().value() : null,
            category.getName().value(),
            category.getDescription(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
}
