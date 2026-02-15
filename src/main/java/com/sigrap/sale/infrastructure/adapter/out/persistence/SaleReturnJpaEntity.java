package com.sigrap.sale.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * JPA entity for persisting sale return data.
 * This is a pure persistence model with no business logic.
 * Maps to the "sale_returns" table in the database.
 */
@Entity
@Table(name = "sale_returns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleReturnJpaEntity {

    /**
     * Unique identifier for the sale return.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique return number for tracking.
     * Cannot be null and must be unique.
     */
    @Column(name = "return_number", nullable = false, unique = true, length = 50)
    private String returnNumber;

    /**
     * Original sale identifier.
     * Cannot be null.
     */
    @Column(name = "sale_id", nullable = false)
    private Long saleId;

    /**
     * Date and time when the return occurred.
     * Cannot be null.
     */
    @Column(name = "return_date", nullable = false)
    private LocalDateTime returnDate;

    /**
     * Reason for the return.
     * Cannot be null.
     */
    @Column(nullable = false, length = 500)
    private String reason;

    /**
     * Current status of the return.
     * Cannot be null.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * Refund amount for the return.
     * Cannot be null.
     */
    @Column(name = "refund_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal refundAmount;

    /**
     * Additional notes about the return.
     * Optional field.
     */
    @Column(length = 1000)
    private String notes;

    /**
     * Timestamp of when the return was created.
     * Automatically set during entity creation.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of when the return was last updated.
     * Automatically updated when the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
