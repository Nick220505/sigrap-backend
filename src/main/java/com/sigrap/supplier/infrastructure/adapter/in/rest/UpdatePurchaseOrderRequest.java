package com.sigrap.supplier.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request DTO for purchase order updates.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param expectedDeliveryDate the updated expected delivery date (optional)
 * @param notes the updated notes (optional, max 1000 characters)
 */
public record UpdatePurchaseOrderRequest(
    LocalDate expectedDeliveryDate,
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes
) {}
