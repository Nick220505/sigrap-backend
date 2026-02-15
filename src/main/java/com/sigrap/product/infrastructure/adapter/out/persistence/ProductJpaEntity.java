package com.sigrap.product.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * JPA entity for persisting product data.
 * This is a pure persistence model with no business logic.
 * Maps to the "products" table in the database.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductJpaEntity {

    /**
     * Unique identifier for the product.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the product.
     * Must be unique and cannot be null.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String name;

    /**
     * Optional description of the product.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Cost price of the product.
     * Cannot be null.
     */
    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;

    /**
     * Sale price of the product.
     * Cannot be null.
     */
    @Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;

    /**
     * Current stock quantity.
     * Cannot be null.
     */
    @Column(nullable = false)
    private Integer stock;

    /**
     * Minimum stock threshold for alerts.
     * Cannot be null.
     */
    @Column(name = "minimum_stock_threshold", nullable = false)
    private Integer minimumStockThreshold;

    /**
     * Foreign key reference to the category.
     * Can be null if product has no category.
     */
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * Timestamp of when the product was created.
     * Automatically set during entity creation.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of when the product was last updated.
     * Automatically updated when the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
