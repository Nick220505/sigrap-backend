package com.sigrap.sale.infrastructure.adapter.in.rest;

import com.sigrap.sale.domain.model.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request DTO for sale operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param saleNumber the sale number (required, max 50 characters)
 * @param customerId the customer identifier (required)
 * @param employeeId the employee identifier (required)
 * @param saleDate the date of the sale (required)
 * @param paymentMethod the payment method used (required)
 * @param notes additional notes (optional, max 1000 characters)
 */
public record SaleRequest(
    @NotBlank(message = "Sale number is required")
    @Size(max = 50, message = "Sale number cannot exceed 50 characters")
    String saleNumber,
    
    @NotNull(message = "Customer ID is required")
    Long customerId,
    
    @NotNull(message = "Employee ID is required")
    Long employeeId,
    
    @NotNull(message = "Sale date is required")
    LocalDateTime saleDate,
    
    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod,
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes
) {}
