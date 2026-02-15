package com.sigrap.supplier.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for purchase order creation.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param orderNumber the unique order number (required, max 50 characters)
 * @param supplierId the supplier identifier (required, must be positive)
 * @param orderDate the date the order was placed (required)
 * @param expectedDeliveryDate the expected delivery date (optional)
 * @param totalAmount the total amount of the order (required, must be positive)
 * @param notes additional notes (optional, max 1000 characters)
 */
public record PurchaseOrderRequest(
    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number cannot exceed 50 characters")
    String orderNumber,
    
    @NotNull(message = "Supplier ID is required")
    @Positive(message = "Supplier ID must be positive")
    Long supplierId,
    
    @NotNull(message = "Order date is required")
    LocalDate orderDate,
    
    LocalDate expectedDeliveryDate,
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    BigDecimal totalAmount,
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes
) {}
