package com.sigrap.product.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value object representing a product price (cost or sale price).
 * Immutable and self-validating.
 */
public record ProductPrice(BigDecimal value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the price is valid (not null and not negative).
     *
     * @throws IllegalArgumentException if the value is null or negative
     */
    public ProductPrice {
        if (value == null) {
            throw new IllegalArgumentException("Product price cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
    }
    
    /**
     * Checks if this price is greater than another price.
     *
     * @param other the price to compare with
     * @return true if this price is greater than the other price
     */
    public boolean isGreaterThan(ProductPrice other) {
        Objects.requireNonNull(other, "Other price cannot be null");
        return this.value.compareTo(other.value) > 0;
    }
    
    /**
     * Checks if this price is less than another price.
     *
     * @param other the price to compare with
     * @return true if this price is less than the other price
     */
    public boolean isLessThan(ProductPrice other) {
        Objects.requireNonNull(other, "Other price cannot be null");
        return this.value.compareTo(other.value) < 0;
    }
}
