package com.sigrap.supplier.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for purchase order operations.
 * Represents the purchase order data returned to REST clients.
 *
 * @param id the purchase order identifier
 * @param orderNumber the unique order number
 * @param supplierId the supplier identifier
 * @param orderDate the date the order was placed
 * @param expectedDeliveryDate the expected delivery date
 * @param status the current status
 * @param totalAmount the total amount of the order
 * @param notes additional notes
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
@Schema(description = "Purchase order information returned in API responses")
public record PurchaseOrderResponse(
    @Schema(description = "Unique identifier of the purchase order", example = "1")
    Long id,
    
    @Schema(description = "Unique order number", example = "PO-2024-001")
    String orderNumber,
    
    @Schema(description = "Supplier ID for this purchase order", example = "1")
    Long supplierId,
    
    @Schema(description = "Date when the order was placed", example = "2024-01-15")
    LocalDate orderDate,
    
    @Schema(description = "Expected delivery date", example = "2024-01-30")
    LocalDate expectedDeliveryDate,
    
    @Schema(description = "Current status of the purchase order", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "RECEIVED", "CANCELLED"})
    String status,
    
    @Schema(description = "Total amount of the purchase order", example = "5000.00")
    BigDecimal totalAmount,
    
    @Schema(description = "Additional notes or comments", example = "Urgent delivery required")
    String notes,
    
    @Schema(description = "Timestamp when the purchase order was created", example = "2024-01-15T10:30:00")
    LocalDateTime createdAt,
    
    @Schema(description = "Timestamp when the purchase order was last updated", example = "2024-01-20T14:45:00")
    LocalDateTime updatedAt
) {}
