package com.sigrap.supplier.infrastructure.adapter.in.rest;

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
public record PurchaseOrderResponse(
    Long id,
    String orderNumber,
    Long supplierId,
    LocalDate orderDate,
    LocalDate expectedDeliveryDate,
    String status,
    BigDecimal totalAmount,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
