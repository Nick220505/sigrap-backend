package com.sigrap.product.infrastructure.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for creating or updating a product")
public record ProductRequest(
    @Schema(
        description = "Product name - must be unique across the system",
        example = "Laptop Dell XPS 15",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 255
    )
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    String name,
    
    @Schema(
        description = "Detailed description of the product",
        example = "High-performance laptop with Intel i7 processor, 16GB RAM, and 512GB SSD"
    )
    String description,
    
    @Schema(
        description = "Cost price of the product (purchase/wholesale price)",
        example = "899.99",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minimum = "0.01"
    )
    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost price must be greater than 0")
    BigDecimal costPrice,
    
    @Schema(
        description = "Sale price of the product (retail price)",
        example = "1299.99",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minimum = "0.01"
    )
    @NotNull(message = "Sale price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Sale price must be greater than 0")
    BigDecimal salePrice,
    
    @Schema(
        description = "Current stock quantity available",
        example = "50",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minimum = "0"
    )
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    Integer stock,
    
    @Schema(
        description = "Minimum stock threshold for low stock alerts",
        example = "10",
        requiredMode = Schema.RequiredMode.REQUIRED,
        minimum = "0"
    )
    @NotNull(message = "Minimum stock threshold is required")
    @Min(value = 0, message = "Minimum stock threshold cannot be negative")
    Integer minimumStockThreshold,
    
    @Schema(
        description = "Category ID to which this product belongs (optional)",
        example = "1"
    )
    Long categoryId
) {}
