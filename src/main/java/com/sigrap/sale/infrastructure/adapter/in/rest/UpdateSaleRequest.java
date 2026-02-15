package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.sigrap.sale.domain.model.PaymentMethod;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating sale operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param paymentMethod the payment method used (optional)
 * @param notes additional notes (optional, max 1000 characters)
 */
public record UpdateSaleRequest(
    PaymentMethod paymentMethod,
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes
) {}
