package com.sigrap.customer.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for customer operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param fullName the customer's full name (required, max 255 characters)
 * @param documentId the customer's document ID (optional, max 50 characters)
 * @param email the customer's email address (required, valid email format, max 255 characters)
 * @param phoneNumber the customer's phone number (optional, max 20 characters)
 * @param address the customer's physical address (optional, max 500 characters)
 */
@Schema(description = "Request payload for creating or updating a customer")
public record CustomerRequest(
    @Schema(
        description = "Customer's full name",
        example = "Juan Pérez García",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 255
    )
    @NotBlank(message = "Customer full name is required")
    @Size(max = 255, message = "Customer full name cannot exceed 255 characters")
    String fullName,
    
    @Schema(
        description = "Customer's document ID (e.g., national ID, passport number)",
        example = "12345678A",
        maxLength = 50
    )
    @Size(max = 50, message = "Document ID cannot exceed 50 characters")
    String documentId,
    
    @Schema(
        description = "Customer's email address - must be unique",
        example = "juan.perez@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 255
    )
    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be a valid email address")
    @Size(max = 255, message = "Customer email cannot exceed 255 characters")
    String email,
    
    @Schema(
        description = "Customer's phone number",
        example = "+34 612 345 678",
        maxLength = 20
    )
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    String phoneNumber,
    
    @Schema(
        description = "Customer's physical address",
        example = "Calle Mayor 123, 28013 Madrid, España",
        maxLength = 500
    )
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    String address
) {}
