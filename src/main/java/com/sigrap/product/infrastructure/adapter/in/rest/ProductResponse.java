package com.sigrap.product.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "Product information returned in API responses")
public record ProductResponse(
    @Schema(
        description = "Unique identifier of the product",
        example = "1"
    )
    Long id,
    
    @Schema(
        description = "Product name",
        example = "Laptop Dell XPS 15"
    )
    String name,
    
    @Schema(
        description = "Detailed description of the product",
        example = "High-performance laptop with Intel i7 processor, 16GB RAM, and 512GB SSD"
    )
    String description,
    
    @Schema(
        description = "Cost price of the product (purchase/wholesale price)",
        example = "899.99"
    )
    BigDecimal costPrice,
    
    @Schema(
        description = "Sale price of the product (retail price)",
        example = "1299.99"
    )
    BigDecimal salePrice,
    
    @Schema(
        description = "Current stock quantity available",
        example = "50"
    )
    Integer stock,
    
    @Schema(
        description = "Minimum stock threshold for low stock alerts",
        example = "10"
    )
    Integer minimumStockThreshold,
    
    @Schema(
        description = "Category ID to which this product belongs",
        example = "1"
    )
    Long categoryId,
    
    @Schema(
        description = "Timestamp when the product was created",
        example = "2024-01-15T10:30:00"
    )
    LocalDateTime createdAt,
    
    @Schema(
        description = "Timestamp when the product was last updated",
        example = "2024-01-20T14:45:00"
    )
    LocalDateTime updatedAt
) {}
