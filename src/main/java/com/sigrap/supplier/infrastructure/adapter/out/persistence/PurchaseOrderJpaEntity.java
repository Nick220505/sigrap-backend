package com.sigrap.supplier.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * JPA entity for persisting purchase order data.
 * This is a pure persistence model with no business logic.
 * Maps to the "purchase_orders" table in the database.
 */
@Entity
@Table(name = "purchase_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderJpaEntity {

    /**
     * Unique identifier for the purchase order.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique order number.
     * Cannot be null.
     */
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    /**
     * Supplier identifier.
     * Cannot be null.
     */
    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    /**
     * Date the order was placed.
     * Cannot be null.
     */
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    /**
     * Expected delivery date.
     */
    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    /**
     * Current status of the purchase order.
     * Cannot be null.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PurchaseOrderStatusJpa status;

    /**
     * Total amount of the order.
     * Cannot be null.
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Additional notes.
     */
    @Column(length = 1000)
    private String notes;

    /**
     * Timestamp of when the purchase order was created.
     * Automatically set during entity creation.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of when the purchase order was last updated.
     * Automatically updated when the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * JPA enum for purchase order status.
     * Mirrors the domain PurchaseOrderStatus enum.
     */
    public enum PurchaseOrderStatusJpa {
        PENDING,
        APPROVED,
        RECEIVED,
        CANCELLED
    }
}
