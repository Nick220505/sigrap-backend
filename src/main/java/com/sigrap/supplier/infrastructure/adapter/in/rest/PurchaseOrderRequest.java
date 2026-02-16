package com.sigrap.supplier.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for creating a purchase order")
public record PurchaseOrderRequest(
    @Schema(description = "Unique order number", example = "PO-2024-001", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number cannot exceed 50 characters")
    String orderNumber,
    
    @Schema(description = "Supplier ID for this purchase order", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Supplier ID is required")
    @Positive(message = "Supplier ID must be positive")
    Long supplierId,
    
    @Schema(description = "Date when the order was placed", example = "2024-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Order date is required")
    LocalDate orderDate,
    
    @Schema(description = "Expected delivery date", example = "2024-01-30")
    LocalDate expectedDeliveryDate,
    
    @Schema(description = "Total amount of the purchase order", example = "5000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    BigDecimal totalAmount,
    
    @Schema(description = "Additional notes or comments", example = "Urgent delivery required", maxLength = 1000)
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes
) {}
