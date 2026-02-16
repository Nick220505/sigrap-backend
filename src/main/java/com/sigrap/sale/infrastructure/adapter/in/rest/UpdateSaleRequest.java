package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.sigrap.sale.domain.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating a sale")
public record UpdateSaleRequest(
    @Schema(description = "Updated payment method", example = "CREDIT_CARD", allowableValues = {"CASH", "CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER"})
    PaymentMethod paymentMethod,
    
    @Schema(description = "Updated notes", example = "Payment method changed per customer request", maxLength = 1000)
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes
) {}
