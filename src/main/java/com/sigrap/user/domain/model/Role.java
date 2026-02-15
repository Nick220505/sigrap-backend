package com.sigrap.user.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Domain entity representing a role in the system.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 *
 * <p>A role represents a collection of permissions that can be assigned to users,
 * defining what actions they can perform in the system.</p>
 */
public class Role {
    private final RoleId id;
    private RoleName name;
    private final Set<Permission> permissions;
    private String description;

    /**
     * Constructor for creating a new role (without ID).
     * Used when creating a role before persistence.
     *
     * @param name the role name (required)
     * @param description the role description (optional)
     */
    public Role(RoleName name, String description) {
        this(null, name, new HashSet<>(), description);
    }

    /**
     * Full constructor for reconstituting a role from persistence.
     *
     * @param id the role identifier
     * @param name the role name (required)
     * @param permissions the set of permissions for this role
     * @param description the role description (optional)
     */
    public Role(RoleId id, RoleName name, Set<Permission> permissions, String description) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Role name cannot be null");
        this.permissions = new HashSet<>(permissions != null ? permissions : new HashSet<>());
        this.description = description;
    }

    /**
     * Updates the name of this role.
     *
     * @param newName the new role name (required)
     */
    public void updateName(RoleName newName) {
        Objects.requireNonNull(newName, "Role name cannot be null");
        this.name = newName;
    }

    /**
     * Updates the description of this role.
     *
     * @param newDescription the new description (optional)
     */
    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    /**
     * Adds a permission to this role.
     *
     * @param permission the permission to add (required)
     */
    public void addPermission(Permission permission) {
        Objects.requireNonNull(permission, "Permission cannot be null");
        this.permissions.add(permission);
    }

    /**
     * Removes a permission from this role.
     *
     * @param permission the permission to remove (required)
     */
    public void removePermission(Permission permission) {
        Objects.requireNonNull(permission, "Permission cannot be null");
        this.permissions.remove(permission);
    }

    /**
     * Checks if this role has a specific permission.
     *
     * @param permission the permission to check
     * @return true if the role has the permission, false otherwise
     */
    public boolean hasPermission(Permission permission) {
        return this.permissions.contains(permission);
    }

    /**
     * Checks if this role is new (not yet persisted).
     *
     * @return true if the role has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    // Getters (no setters - controlled mutation through business methods)

    public RoleId getId() {
        return id;
    }

    public RoleName getName() {
        return name;
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name=" + name +
                ", permissionCount=" + permissions.size() +
                ", description='" + description + '\'' +
                '}';
    }
}
