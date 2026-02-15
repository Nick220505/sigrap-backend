package com.sigrap.supplier.infrastructure.adapter.in.rest;

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
public record SupplierRequest(
    @NotBlank(message = "Supplier name is required")
    @Size(max = 200, message = "Supplier name cannot exceed 200 characters")
    String name,
    
    @Size(max = 200, message = "Contact name cannot exceed 200 characters")
    String contactName,
    
    @NotBlank(message = "Supplier email is required")
    @Email(message = "Supplier email must be valid")
    @Size(max = 100, message = "Supplier email cannot exceed 100 characters")
    String email,
    
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    String phone,
    
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    String address
) {}
