package com.sigrap.sale.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for sale return operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param returnNumber the return number (required, max 50 characters)
 * @param saleId the original sale identifier (required)
 * @param returnDate the date of the return (required)
 * @param reason the reason for the return (required, max 500 characters)
 * @param refundAmount the refund amount (required, must be non-negative)
 * @param notes additional notes (optional, max 1000 characters)
 */
public record SaleReturnRequest(
    @NotBlank(message = "Return number is required")
    @Size(max = 50, message = "Return number cannot exceed 50 characters")
    String returnNumber,
    
    @NotNull(message = "Sale ID is required")
    Long saleId,
    
    @NotNull(message = "Return date is required")
    LocalDateTime returnDate,
    
    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    String reason,
    
    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Refund amount cannot be negative")
    BigDecimal refundAmount,
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes
) {}
