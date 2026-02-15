package com.sigrap.supplier.application.port.in.command;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Command for creating a new purchase order.
 * This is an immutable data carrier that represents the user's intent to create a purchase order.
 *
 * @param orderNumber the unique order number
 * @param supplierId the supplier identifier
 * @param orderDate the date the order was placed
 * @param expectedDeliveryDate the expected delivery date (optional)
 * @param totalAmount the total amount of the order
 * @param notes additional notes (optional)
 */
public record CreatePurchaseOrderCommand(
    String orderNumber,
    Long supplierId,
    LocalDate orderDate,
    LocalDate expectedDeliveryDate,
    BigDecimal totalAmount,
    String notes
) {}
