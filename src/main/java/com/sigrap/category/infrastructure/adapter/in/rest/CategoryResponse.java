package com.sigrap.category.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Response DTO for category operations.
 * Represents the category data returned to REST clients.
 *
 * @param id the category identifier
 * @param name the category name
 * @param description the category description
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
@Schema(
    description = "Category response with all details",
    example = """
        {
            "id": 1,
            "name": "Office Supplies",
            "description": "Items for office use including pens, paper, and folders",
            "createdAt": "2026-02-15T21:00:00",
            "updatedAt": "2026-02-15T21:00:00"
        }
        """
)
public record CategoryResponse(
    @Schema(description = "Unique identifier", example = "1")
    Long id,
    
    @Schema(description = "Category name", example = "Office Supplies")
    String name,
    
    @Schema(description = "Category description", example = "Items for office use including pens, paper, and folders")
    String description,
    
    @Schema(description = "Timestamp when the category was created (ISO 8601 format)", example = "2026-02-15T21:00:00")
    LocalDateTime createdAt,
    
    @Schema(description = "Timestamp when the category was last updated (ISO 8601 format)", example = "2026-02-15T21:00:00")
    LocalDateTime updatedAt
) {}
