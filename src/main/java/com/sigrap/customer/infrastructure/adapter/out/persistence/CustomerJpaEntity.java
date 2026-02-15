package com.sigrap.customer.infrastructure.adapter.out.persistence;

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
 * JPA entity for persisting customer data.
 * This is a pure persistence model with no business logic.
 * Maps to the "customers" table in the database.
 */
@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerJpaEntity {

    /**
     * Unique identifier for the customer.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Full name of the customer.
     * Cannot be null.
     */
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    /**
     * Document ID of the customer (e.g., national ID, passport).
     * Optional field.
     */
    @Column(name = "document_id", length = 50)
    private String documentId;

    /**
     * Email address of the customer.
     * Must be unique and cannot be null.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Phone number of the customer.
     * Optional field.
     */
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    /**
     * Physical address of the customer.
     * Optional field.
     */
    @Column(length = 500)
    private String address;

    /**
     * Timestamp of when the customer was created.
     * Automatically set during entity creation.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of when the customer was last updated.
     * Automatically updated when the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
