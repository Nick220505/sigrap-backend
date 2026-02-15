package com.sigrap.user.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Collections;

/**
 * Domain entity representing a user in the system.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 *
 * <p>A user represents an authenticated entity that can access the application,
 * with associated roles, permissions, and account status.</p>
 */
public class User {
    private final UserId id;
    private Username username;
    private UserEmail email;
    private String hashedPassword;
    private final Set<Role> roles;
    private boolean enabled;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a new user (without ID).
     * Used when creating a user before persistence.
     *
     * @param username the username (required)
     * @param email the email address (required)
     * @param hashedPassword the hashed password (required)
     */
    public User(Username username, UserEmail email, String hashedPassword) {
        this(null, username, email, hashedPassword, new HashSet<>(), true, 
             LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Full constructor for reconstituting a user from persistence.
     *
     * @param id the user identifier
     * @param username the username (required)
     * @param email the email address (required)
     * @param hashedPassword the hashed password (required)
     * @param roles the set of roles assigned to the user
     * @param enabled whether the user account is enabled
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public User(UserId id, Username username, UserEmail email, String hashedPassword,
                Set<Role> roles, boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.hashedPassword = Objects.requireNonNull(hashedPassword, "Password cannot be null");
        this.roles = new HashSet<>(roles != null ? roles : new HashSet<>());
        this.enabled = enabled;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }

    /**
     * Updates the email address for this user.
     *
     * @param newEmail the new email address (required)
     */
    public void updateEmail(UserEmail newEmail) {
        Objects.requireNonNull(newEmail, "Email cannot be null");
        if (!this.email.equals(newEmail)) {
            this.email = newEmail;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the password for this user.
     * The password should already be hashed before calling this method.
     *
     * @param newHashedPassword the new hashed password (required)
     */
    public void updatePassword(String newHashedPassword) {
        Objects.requireNonNull(newHashedPassword, "Password cannot be null");
        if (newHashedPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        this.hashedPassword = newHashedPassword;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Enables this user account.
     * An enabled user can authenticate and access the system.
     */
    public void enable() {
        if (!this.enabled) {
            this.enabled = true;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Disables this user account.
     * A disabled user cannot authenticate or access the system.
     */
    public void disable() {
        if (this.enabled) {
            this.enabled = false;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Adds a role to this user.
     *
     * @param role the role to add (required)
     */
    public void addRole(Role role) {
        Objects.requireNonNull(role, "Role cannot be null");
        if (this.roles.add(role)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Removes a role from this user.
     *
     * @param role the role to remove (required)
     */
    public void removeRole(Role role) {
        Objects.requireNonNull(role, "Role cannot be null");
        if (this.roles.remove(role)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Checks if this user has a specific role.
     *
     * @param role the role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    /**
     * Checks if this user has a specific permission.
     *
     * @param permission the permission to check
     * @return true if any of the user's roles has the permission, false otherwise
     */
    public boolean hasPermission(Permission permission) {
        return this.roles.stream()
                .anyMatch(role -> role.hasPermission(permission));
    }

    /**
     * Checks if this user is new (not yet persisted).
     *
     * @return true if the user has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    // Getters (no setters - controlled mutation through business methods)

    public UserId getId() {
        return id;
    }

    public Username getUsername() {
        return username;
    }

    public UserEmail getEmail() {
        return email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username=" + username +
                ", email=" + email +
                ", enabled=" + enabled +
                ", roleCount=" + roles.size() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
