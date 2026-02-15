package com.sigrap.customer.infrastructure.adapter.in.rest;

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
public record CustomerRequest(
    @NotBlank(message = "Customer full name is required")
    @Size(max = 255, message = "Customer full name cannot exceed 255 characters")
    String fullName,
    
    @Size(max = 50, message = "Document ID cannot exceed 50 characters")
    String documentId,
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be a valid email address")
    @Size(max = 255, message = "Customer email cannot exceed 255 characters")
    String email,
    
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    String phoneNumber,
    
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    String address
) {}
