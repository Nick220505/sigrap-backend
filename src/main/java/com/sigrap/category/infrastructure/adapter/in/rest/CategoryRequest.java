package com.sigrap.category.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for category operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param name the category name (required, max 100 characters)
 * @param description the category description (optional, max 500 characters)
 */
public record CategoryRequest(
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    String name,
    
    @Size(max = 500, message = "Category description cannot exceed 500 characters")
    String description
) {}
