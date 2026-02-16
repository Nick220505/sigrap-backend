package com.sigrap.customer.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response DTO for customer operations.
 * Represents the customer data returned to REST clients.
 *
 * @param id the customer identifier
 * @param fullName the customer's full name
 * @param documentId the customer's document ID
 * @param email the customer's email address
 * @param phoneNumber the customer's phone number
 * @param address the customer's physical address
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
@Schema(description = "Customer information returned in API responses")
public record CustomerResponse(
    @Schema(
        description = "Unique identifier of the customer",
        example = "1"
    )
    Long id,
    
    @Schema(
        description = "Customer's full name",
        example = "Juan Pérez García"
    )
    String fullName,
    
    @Schema(
        description = "Customer's document ID",
        example = "12345678A"
    )
    String documentId,
    
    @Schema(
        description = "Customer's email address",
        example = "juan.perez@example.com"
    )
    String email,
    
    @Schema(
        description = "Customer's phone number",
        example = "+34 612 345 678"
    )
    String phoneNumber,
    
    @Schema(
        description = "Customer's physical address",
        example = "Calle Mayor 123, 28013 Madrid, España"
    )
    String address,
    
    @Schema(
        description = "Timestamp when the customer was created",
        example = "2024-01-15T10:30:00"
    )
    LocalDateTime createdAt,
    
    @Schema(
        description = "Timestamp when the customer was last updated",
        example = "2024-01-20T14:45:00"
    )
    LocalDateTime updatedAt
) {}
