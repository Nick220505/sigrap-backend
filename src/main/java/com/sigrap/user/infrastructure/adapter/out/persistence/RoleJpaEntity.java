package com.sigrap.user.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity for persisting role data.
 * This is a pure persistence model with no business logic.
 * Maps to the "roles" table in the database.
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleJpaEntity {

    /**
     * Unique identifier for the role.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the role.
     * Must be unique and cannot be null.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Optional description of the role.
     */
    @Column(length = 500)
    private String description;

    /**
     * Permissions assigned to this role.
     * Many-to-many relationship with permissions.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<PermissionJpaEntity> permissions = new HashSet<>();
}
