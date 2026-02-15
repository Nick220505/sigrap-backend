package com.sigrap.sale.application.port.in.command;

import java.math.BigDecimal;

/**
 * Command for updating a sale item.
 * This is an immutable data carrier that represents the user's intent to update a sale item.
 *
 * @param quantity the new quantity of the product (optional)
 * @param unitPrice the new unit price of the product (optional)
 */
public record UpdateSaleItemCommand(
    Integer quantity,
    BigDecimal unitPrice
) {}
