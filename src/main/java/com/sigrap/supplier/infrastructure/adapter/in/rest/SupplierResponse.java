package com.sigrap.supplier.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response DTO for supplier operations.
 * Represents the supplier data returned to REST clients.
 *
 * @param id the supplier identifier
 * @param name the supplier name
 * @param contactName the contact person's name
 * @param email the supplier email
 * @param phone the supplier phone number
 * @param address the supplier address
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
@Schema(description = "Supplier information returned in API responses")
public record SupplierResponse(
    @Schema(description = "Unique identifier of the supplier", example = "1")
    Long id,
    
    @Schema(description = "Supplier company name", example = "Acme Supplies Inc.")
    String name,
    
    @Schema(description = "Contact person's full name", example = "María González")
    String contactName,
    
    @Schema(description = "Supplier email address", example = "contact@acmesupplies.com")
    String email,
    
    @Schema(description = "Supplier phone number", example = "+34 912 345 678")
    String phone,
    
    @Schema(description = "Supplier physical address", example = "Calle Industrial 45, 28050 Madrid, España")
    String address,
    
    @Schema(description = "Timestamp when the supplier was created", example = "2024-01-15T10:30:00")
    LocalDateTime createdAt,
    
    @Schema(description = "Timestamp when the supplier was last updated", example = "2024-01-20T14:45:00")
    LocalDateTime updatedAt
) {}
