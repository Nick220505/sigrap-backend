package com.sigrap.product.application.port.in.command;

import java.math.BigDecimal;

/**
 * Command for updating an existing product.
 * This is an immutable data carrier that represents the user's intent to update a product.
 *
 * @param name the new name of the product
 * @param description the new description of the product (optional)
 * @param costPrice the new cost price of the product
 * @param salePrice the new sale price of the product
 * @param stock the new stock quantity
 * @param minimumStockThreshold the new minimum stock threshold
 * @param categoryId the new category identifier (optional)
 */
public record UpdateProductCommand(
    String name,
    String description,
    BigDecimal costPrice,
    BigDecimal salePrice,
    Integer stock,
    Integer minimumStockThreshold,
    Long categoryId
) {}
