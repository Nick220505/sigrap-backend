package com.sigrap.supplier.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * JPA entity for persisting supplier data.
 * This is a pure persistence model with no business logic.
 * Maps to the "suppliers" table in the database.
 */
@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierJpaEntity {

    /**
     * Unique identifier for the supplier.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the supplier.
     * Cannot be null.
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Contact person's name.
     */
    @Column(name = "contact_name", length = 200)
    private String contactName;

    /**
     * Supplier's email address.
     * Must be unique and cannot be null.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Supplier's phone number.
     */
    @Column(length = 20)
    private String phone;

    /**
     * Supplier's physical address.
     */
    @Column(length = 500)
    private String address;

    /**
     * Timestamp of when the supplier was created.
     * Automatically set during entity creation.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of when the supplier was last updated.
     * Automatically updated when the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
