package com.sigrap.product.infrastructure.adapter.in.rest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for product operations.
 * Represents the product data returned to REST clients.
 *
 * @param id the product identifier
 * @param name the product name
 * @param description the product description
 * @param costPrice the cost price
 * @param salePrice the sale price
 * @param stock the stock quantity
 * @param minimumStockThreshold the minimum stock threshold
 * @param categoryId the category identifier
 * @param createdAt the creation timestamp
 * @param updatedAt the last update timestamp
 */
public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal costPrice,
    BigDecimal salePrice,
    Integer stock,
    Integer minimumStockThreshold,
    Long categoryId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
