package com.sigrap.product.application.port.in.command;

import java.math.BigDecimal;

/**
 * Command for creating a new product.
 * This is an immutable data carrier that represents the user's intent to create a product.
 *
 * @param name the name of the product to create
 * @param description the description of the product (optional)
 * @param costPrice the cost price of the product
 * @param salePrice the sale price of the product
 * @param stock the initial stock quantity
 * @param minimumStockThreshold the minimum stock threshold for alerts
 * @param categoryId the category identifier (optional)
 */
public record CreateProductCommand(
    String name,
    String description,
    BigDecimal costPrice,
    BigDecimal salePrice,
    Integer stock,
    Integer minimumStockThreshold,
    Long categoryId
) {}
