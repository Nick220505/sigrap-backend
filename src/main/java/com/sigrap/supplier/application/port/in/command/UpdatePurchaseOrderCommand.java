package com.sigrap.supplier.application.port.in.command;

import java.time.LocalDate;

/**
 * Command for updating an existing purchase order.
 * This is an immutable data carrier that represents the user's intent to update a purchase order.
 *
 * @param expectedDeliveryDate the updated expected delivery date (optional)
 * @param notes the updated notes (optional)
 */
public record UpdatePurchaseOrderCommand(
    LocalDate expectedDeliveryDate,
    String notes
) {}
