package com.sigrap.supplier.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for supplier operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param name the supplier name (required, max 200 characters)
 * @param contactName the contact person's name (optional, max 200 characters)
 * @param email the supplier email (required, valid email format, max 100 characters)
 * @param phone the supplier phone number (optional, max 20 characters)
 * @param address the supplier address (optional, max 500 characters)
 */
@Schema(description = "Request payload for creating or updating a supplier")
public record SupplierRequest(
    @Schema(
        description = "Supplier company name",
        example = "Acme Supplies Inc.",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 200
    )
    @NotBlank(message = "Supplier name is required")
    @Size(max = 200, message = "Supplier name cannot exceed 200 characters")
    String name,
    
    @Schema(
        description = "Contact person's full name",
        example = "María González",
        maxLength = 200
    )
    @Size(max = 200, message = "Contact name cannot exceed 200 characters")
    String contactName,
    
    @Schema(
        description = "Supplier email address",
        example = "contact@acmesupplies.com",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 100
    )
    @NotBlank(message = "Supplier email is required")
    @Email(message = "Supplier email must be valid")
    @Size(max = 100, message = "Supplier email cannot exceed 100 characters")
    String email,
    
    @Schema(
        description = "Supplier phone number",
        example = "+34 912 345 678",
        maxLength = 20
    )
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    String phone,
    
    @Schema(
        description = "Supplier physical address",
        example = "Calle Industrial 45, 28050 Madrid, España",
        maxLength = 500
    )
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    String address
) {}
