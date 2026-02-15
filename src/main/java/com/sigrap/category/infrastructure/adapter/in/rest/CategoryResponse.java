package com.sigrap.category.infrastructure.adapter.in.rest;

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
public record CategoryResponse(
    Long id,
    String name,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
