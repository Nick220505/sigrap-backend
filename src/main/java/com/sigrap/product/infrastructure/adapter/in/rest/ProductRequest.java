package com.sigrap.product.infrastructure.adapter.in.rest;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Request DTO for product operations.
 * Includes validation annotations for input validation at the REST adapter level.
 *
 * @param name the product name (required, max 255 characters)
 * @param description the product description (optional)
 * @param costPrice the cost price (required, must be positive)
 * @param salePrice the sale price (required, must be positive)
 * @param stock the stock quantity (required, must be non-negative)
 * @param minimumStockThreshold the minimum stock threshold (required, must be non-negative)
 * @param categoryId the category identifier (optional)
 */
public record ProductRequest(
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    String name,
    
    String description,
    
    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost price must be greater than 0")
    BigDecimal costPrice,
    
    @NotNull(message = "Sale price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Sale price must be greater than 0")
    BigDecimal salePrice,
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    Integer stock,
    
    @NotNull(message = "Minimum stock threshold is required")
    @Min(value = 0, message = "Minimum stock threshold cannot be negative")
    Integer minimumStockThreshold,
    
    Long categoryId
) {}
