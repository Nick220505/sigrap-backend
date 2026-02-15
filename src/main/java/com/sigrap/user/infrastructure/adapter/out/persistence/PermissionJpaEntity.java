package com.sigrap.user.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity for persisting permission data.
 * This is a pure persistence model with no business logic.
 * Maps to the "permissions" table in the database.
 */
@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionJpaEntity {

    /**
     * Unique identifier for the permission.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the permission.
     * Must be unique and cannot be null.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Resource this permission applies to.
     * Cannot be null.
     */
    @Column(nullable = false, length = 50)
    private String resource;

    /**
     * Action this permission allows.
     * Cannot be null.
     */
    @Column(nullable = false, length = 50)
    private String action;
}
