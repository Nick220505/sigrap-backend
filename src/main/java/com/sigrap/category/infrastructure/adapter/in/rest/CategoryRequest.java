package com.sigrap.category.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for category operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param name the category name (required, max 100 characters)
 * @param description the category description (optional, max 500 characters)
 */
@Schema(
    description = "Request body for creating or updating a category",
    example = """
        {
            "name": "Office Supplies",
            "description": "Items for office use including pens, paper, and folders"
        }
        """
)
public record CategoryRequest(
    @Schema(
        description = "Category name - must be unique across the system",
        example = "Office Supplies",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 100
    )
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    String name,
    
    @Schema(
        description = "Optional detailed description of the category",
        example = "Items for office use including pens, paper, and folders",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        maxLength = 500
    )
    @Size(max = 500, message = "Category description cannot exceed 500 characters")
    String description
) {}
