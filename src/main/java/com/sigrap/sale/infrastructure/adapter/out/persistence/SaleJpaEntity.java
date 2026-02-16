package com.sigrap.sale.infrastructure.adapter.out.persistence;

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
 * JPA entity for persisting sale data.
 * This is a pure persistence model with no business logic.
 * Maps to the "sales" table in the database.
 */
@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleJpaEntity {

    /**
     * Unique identifier for the sale.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique sale number for tracking.
     * Cannot be null and must be unique.
     */
    @Column(name = "sale_number", nullable = false, unique = true, length = 50)
    private String saleNumber;

    /**
     * Customer identifier.
     * Cannot be null.
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * Employee identifier who processed the sale.
     * Cannot be null.
     */
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    /**
     * Date and time when the sale occurred.
     * Cannot be null.
     */
    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;

    /**
     * Total amount of the sale.
     * Cannot be null.
     */
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Payment method used for the sale.
     * Cannot be null.
     */
    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    /**
     * Current status of the sale.
     * Cannot be null.
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * Additional notes about the sale.
     * Optional field.
     */
    @Column(length = 1000)
    private String notes;

    /**
     * Timestamp of when the sale was created.
     * Automatically set during entity creation.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of when the sale was last updated.
     * Automatically updated when the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
