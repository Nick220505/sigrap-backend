package com.sigrap.category.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a product category.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 */
public class Category {
    private final CategoryId id;
    private CategoryName name;
    private String description;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a new category (without ID).
     * Used when creating a category before persistence.
     *
     * @param name the category name (required)
     * @param description the category description (optional)
     */
    public Category(CategoryName name, String description) {
        this(null, name, description, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Full constructor for reconstituting a category from persistence.
     *
     * @param id the category identifier
     * @param name the category name (required)
     * @param description the category description (optional)
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public Category(CategoryId id, CategoryName name, String description, 
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Category name cannot be null");
        this.description = description;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }

    /**
     * Updates the category name.
     * Business rule: name cannot be null and must be different from current name.
     *
     * @param newName the new category name
     */
    public void updateName(CategoryName newName) {
        Objects.requireNonNull(newName, "New name cannot be null");
        if (!this.name.equals(newName)) {
            this.name = newName;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the category description.
     *
     * @param newDescription the new description (can be null)
     */
    public void updateDescription(String newDescription) {
        if (!Objects.equals(this.description, newDescription)) {
            this.description = newDescription;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Checks if this category is new (not yet persisted).
     *
     * @return true if the category has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    // Getters (no setters - controlled mutation through business methods)

    public CategoryId getId() {
        return id;
    }

    public CategoryName getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name=" + name +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
