package com.sigrap.sale.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain entity representing a sale item.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 *
 * <p>A sale item represents a single product line in a sale transaction,
 * including quantity, unit price, and calculated subtotal.</p>
 */
public class SaleItem {
    private final SaleItemId id;
    private final SaleId saleId;
    private final Long productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    /**
     * Constructor for creating a new sale item (without ID).
     * Used when creating a sale item before persistence.
     *
     * @param saleId the sale identifier this item belongs to (required)
     * @param productId the product identifier (required)
     * @param quantity the quantity of the product (must be positive)
     * @param unitPrice the unit price of the product (must be non-negative)
     */
    public SaleItem(SaleId saleId, Long productId, int quantity, BigDecimal unitPrice) {
        this(null, saleId, productId, quantity, unitPrice);
    }

    /**
     * Full constructor for reconstituting a sale item from persistence.
     *
     * @param id the sale item identifier
     * @param saleId the sale identifier this item belongs to (required)
     * @param productId the product identifier (required)
     * @param quantity the quantity of the product (must be positive)
     * @param unitPrice the unit price of the product (must be non-negative)
     */
    public SaleItem(SaleItemId id, SaleId saleId, Long productId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.saleId = Objects.requireNonNull(saleId, "Sale ID cannot be null");
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
        
        this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price cannot be null");
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        
        this.subtotal = calculateSubtotal();
    }

    /**
     * Calculates the subtotal for this item (quantity * unit price).
     *
     * @return the calculated subtotal
     */
    public BigDecimal calculateSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Updates the quantity of this sale item.
     * Automatically recalculates the subtotal.
     *
     * @param newQuantity the new quantity (must be positive)
     */
    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = newQuantity;
        this.subtotal = calculateSubtotal();
    }

    /**
     * Updates the unit price of this sale item.
     * Automatically recalculates the subtotal.
     *
     * @param newUnitPrice the new unit price (must be non-negative)
     */
    public void updateUnitPrice(BigDecimal newUnitPrice) {
        Objects.requireNonNull(newUnitPrice, "Unit price cannot be null");
        if (newUnitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        this.unitPrice = newUnitPrice;
        this.subtotal = calculateSubtotal();
    }

    /**
     * Checks if this sale item is new (not yet persisted).
     *
     * @return true if the sale item has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    // Getters (no setters - controlled mutation through business methods)

    public SaleItemId getId() {
        return id;
    }

    public SaleId getSaleId() {
        return saleId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleItem saleItem = (SaleItem) o;
        return Objects.equals(id, saleItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SaleItem{" +
                "id=" + id +
                ", saleId=" + saleId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + subtotal +
                '}';
    }
}
