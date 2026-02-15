package com.sigrap.sale.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity for persisting sale item data.
 * This is a pure persistence model with no business logic.
 * Maps to the "sale_items" table in the database.
 */
@Entity
@Table(name = "sale_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItemJpaEntity {

    /**
     * Unique identifier for the sale item.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Sale identifier this item belongs to.
     * Cannot be null.
     */
    @Column(name = "sale_id", nullable = false)
    private Long saleId;

    /**
     * Product identifier.
     * Cannot be null.
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * Quantity of the product.
     * Cannot be null and must be positive.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Unit price of the product at the time of sale.
     * Cannot be null.
     */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Subtotal for this item (quantity * unit price).
     * Cannot be null.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}
