package com.sigrap.supplier.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request DTO for purchase order updates.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param expectedDeliveryDate the updated expected delivery date (optional)
 * @param notes the updated notes (optional, max 1000 characters)
 */
@Schema(description = "Request payload for updating a purchase order")
public record UpdatePurchaseOrderRequest(
    @Schema(description = "Updated expected delivery date", example = "2024-02-15")
    LocalDate expectedDeliveryDate,
    
    @Schema(description = "Updated notes or comments", example = "Delivery postponed due to supplier delay", maxLength = 1000)
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes
) {}
