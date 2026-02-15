package com.sigrap.user.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity for persisting user data.
 * This is a pure persistence model with no business logic.
 * Maps to the "users" table in the database.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJpaEntity {

    /**
     * Unique identifier for the user.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username for authentication.
     * Must be unique and cannot be null.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Email address of the user.
     * Must be unique and cannot be null.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Hashed password for authentication.
     * Cannot be null.
     */
    @Column(nullable = false, name = "hashed_password")
    private String hashedPassword;

    /**
     * Whether the user account is enabled.
     * Disabled users cannot authenticate.
     */
    @Column(nullable = false)
    private boolean enabled;

    /**
     * Roles assigned to this user.
     * Many-to-many relationship with roles.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<RoleJpaEntity> roles = new HashSet<>();

    /**
     * Timestamp of when the user was created.
     * Automatically set during entity creation.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of when the user was last updated.
     * Automatically updated when the entity is modified.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
