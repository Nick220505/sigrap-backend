package com.sigrap.user.domain.model;

import java.util.Objects;

/**
 * Domain entity representing a permission in the system.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 *
 * <p>A permission represents a specific action that can be performed on a resource,
 * such as "READ_PRODUCT", "CREATE_SALE", etc.</p>
 */
public class Permission {
    private final PermissionId id;
    private PermissionName name;
    private String resource;
    private String action;

    /**
     * Constructor for creating a new permission (without ID).
     * Used when creating a permission before persistence.
     *
     * @param name the permission name (required)
     * @param resource the resource this permission applies to (required)
     * @param action the action this permission allows (required)
     */
    public Permission(PermissionName name, String resource, String action) {
        this(null, name, resource, action);
    }

    /**
     * Full constructor for reconstituting a permission from persistence.
     *
     * @param id the permission identifier
     * @param name the permission name (required)
     * @param resource the resource this permission applies to (required)
     * @param action the action this permission allows (required)
     */
    public Permission(PermissionId id, PermissionName name, String resource, String action) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Permission name cannot be null");
        this.resource = Objects.requireNonNull(resource, "Resource cannot be null");
        this.action = Objects.requireNonNull(action, "Action cannot be null");
        
        if (resource.isBlank()) {
            throw new IllegalArgumentException("Resource cannot be blank");
        }
        if (action.isBlank()) {
            throw new IllegalArgumentException("Action cannot be blank");
        }
    }

    /**
     * Updates the name of this permission.
     *
     * @param newName the new permission name (required)
     */
    public void updateName(PermissionName newName) {
        Objects.requireNonNull(newName, "Permission name cannot be null");
        this.name = newName;
    }

    /**
     * Updates the resource for this permission.
     *
     * @param newResource the new resource (required)
     */
    public void updateResource(String newResource) {
        Objects.requireNonNull(newResource, "Resource cannot be null");
        if (newResource.isBlank()) {
            throw new IllegalArgumentException("Resource cannot be blank");
        }
        this.resource = newResource;
    }

    /**
     * Updates the action for this permission.
     *
     * @param newAction the new action (required)
     */
    public void updateAction(String newAction) {
        Objects.requireNonNull(newAction, "Action cannot be null");
        if (newAction.isBlank()) {
            throw new IllegalArgumentException("Action cannot be blank");
        }
        this.action = newAction;
    }

    /**
     * Checks if this permission is new (not yet persisted).
     *
     * @return true if the permission has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    // Getters (no setters - controlled mutation through business methods)

    public PermissionId getId() {
        return id;
    }

    public PermissionName getName() {
        return name;
    }

    public String getResource() {
        return resource;
    }

    public String getAction() {
        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name=" + name +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
