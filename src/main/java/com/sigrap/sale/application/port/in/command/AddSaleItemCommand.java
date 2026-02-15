package com.sigrap.sale.application.port.in.command;

import java.math.BigDecimal;

/**
 * Command for adding an item to a sale.
 * This is an immutable data carrier that represents the user's intent to add a sale item.
 *
 * @param saleId the sale identifier
 * @param productId the product identifier
 * @param quantity the quantity of the product
 * @param unitPrice the unit price of the product
 */
public record AddSaleItemCommand(
    Long saleId,
    Long productId,
    int quantity,
    BigDecimal unitPrice
) {}
