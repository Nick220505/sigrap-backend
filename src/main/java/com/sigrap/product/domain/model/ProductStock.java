package com.sigrap.product.domain.model;

/**
 * Value object representing product stock quantity.
 * Immutable and self-validating.
 */
public record ProductStock(Integer value) {
    
    /**
     * Compact constructor with validation.
     * Ensures the stock quantity is valid (not null and not negative).
     *
     * @throws IllegalArgumentException if the value is null or negative
     */
    public ProductStock {
        if (value == null) {
            throw new IllegalArgumentException("Product stock cannot be null");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
    }
    
    /**
     * Checks if the stock is below a given threshold.
     *
     * @param threshold the minimum stock threshold
     * @return true if stock is below the threshold
     */
    public boolean isBelowThreshold(ProductStock threshold) {
        if (threshold == null) {
            return false;
        }
        return this.value < threshold.value;
    }
    
    /**
     * Adds a quantity to the current stock.
     *
     * @param quantity the quantity to add
     * @return a new ProductStock with the updated value
     */
    public ProductStock add(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity to add must be non-negative");
        }
        return new ProductStock(this.value + quantity);
    }
    
    /**
     * Subtracts a quantity from the current stock.
     *
     * @param quantity the quantity to subtract
     * @return a new ProductStock with the updated value
     * @throws IllegalArgumentException if the result would be negative
     */
    public ProductStock subtract(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity to subtract must be non-negative");
        }
        int newValue = this.value - quantity;
        if (newValue < 0) {
            throw new IllegalArgumentException("Cannot subtract " + quantity + " from stock of " + this.value);
        }
        return new ProductStock(newValue);
    }
}
