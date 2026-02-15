package com.sigrap.category.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Category domain entity.
 * Pure unit tests with no Spring context, testing business logic and invariants.
 */
class CategoryTest {

    @Test
    void shouldCreateNewCategoryWithoutId() {
        // Given
        CategoryName name = new CategoryName("Office Supplies");
        String description = "Office and stationery items";

        // When
        Category category = new Category(name, description);

        // Then
        assertNotNull(category);
        assertNull(category.getId());
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
        assertTrue(category.isNew());
    }

    @Test
    void shouldCreateCategoryWithAllFields() {
        // Given
        CategoryId id = new CategoryId(1L);
        CategoryName name = new CategoryName("Electronics");
        String description = "Electronic devices";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        Category category = new Category(id, name, description, createdAt, updatedAt);

        // Then
        assertNotNull(category);
        assertEquals(id, category.getId());
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
        assertEquals(createdAt, category.getCreatedAt());
        assertEquals(updatedAt, category.getUpdatedAt());
        assertFalse(category.isNew());
    }

    @Test
    void shouldThrowExceptionForNullName() {
        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Category(null, "Description")
        );
        assertEquals("Category name cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullCreatedAt() {
        // Given
        CategoryId id = new CategoryId(1L);
        CategoryName name = new CategoryName("Test");

        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Category(id, name, "Description", null, LocalDateTime.now())
        );
        assertEquals("Created at cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullUpdatedAt() {
        // Given
        CategoryId id = new CategoryId(1L);
        CategoryName name = new CategoryName("Test");

        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Category(id, name, "Description", LocalDateTime.now(), null)
        );
        assertEquals("Updated at cannot be null", exception.getMessage());
    }

    @Test
    void shouldAllowNullDescription() {
        // Given
        CategoryName name = new CategoryName("Test Category");

        // When
        Category category = new Category(name, null);

        // Then
        assertNotNull(category);
        assertNull(category.getDescription());
    }

    @Test
    void shouldUpdateNameSuccessfully() {
        // Given
        Category category = new Category(
            new CategoryId(1L),
            new CategoryName("Old Name"),
            "Description",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1)
        );
        LocalDateTime originalUpdatedAt = category.getUpdatedAt();
        CategoryName newName = new CategoryName("New Name");

        // When
        category.updateName(newName);

        // Then
        assertEquals(newName, category.getName());
        assertTrue(category.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldNotUpdateTimestampWhenNameIsTheSame() {
        // Given
        CategoryName name = new CategoryName("Same Name");
        Category category = new Category(
            new CategoryId(1L),
            name,
            "Description",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1)
        );
        LocalDateTime originalUpdatedAt = category.getUpdatedAt();

        // When
        category.updateName(name);

        // Then
        assertEquals(name, category.getName());
        assertEquals(originalUpdatedAt, category.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithNullName() {
        // Given
        Category category = new Category(
            new CategoryId(1L),
            new CategoryName("Test"),
            "Description",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // When & Then
        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> category.updateName(null)
        );
        assertEquals("New name cannot be null", exception.getMessage());
    }

    @Test
    void shouldUpdateDescriptionSuccessfully() {
        // Given
        Category category = new Category(
            new CategoryId(1L),
            new CategoryName("Test"),
            "Old Description",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1)
        );
        LocalDateTime originalUpdatedAt = category.getUpdatedAt();
        String newDescription = "New Description";

        // When
        category.updateDescription(newDescription);

        // Then
        assertEquals(newDescription, category.getDescription());
        assertTrue(category.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldNotUpdateTimestampWhenDescriptionIsTheSame() {
        // Given
        String description = "Same Description";
        Category category = new Category(
            new CategoryId(1L),
            new CategoryName("Test"),
            description,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1)
        );
        LocalDateTime originalUpdatedAt = category.getUpdatedAt();

        // When
        category.updateDescription(description);

        // Then
        assertEquals(description, category.getDescription());
        assertEquals(originalUpdatedAt, category.getUpdatedAt());
    }

    @Test
    void shouldAllowUpdatingDescriptionToNull() {
        // Given
        Category category = new Category(
            new CategoryId(1L),
            new CategoryName("Test"),
            "Some Description",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1)
        );

        // When
        category.updateDescription(null);

        // Then
        assertNull(category.getDescription());
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        // Given
        CategoryId id = new CategoryId(1L);
        Category category1 = new Category(
            id,
            new CategoryName("Category 1"),
            "Description 1",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        Category category2 = new Category(
            id,
            new CategoryName("Category 2"),
            "Description 2",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // When & Then
        assertEquals(category1, category2);
        assertEquals(category1.hashCode(), category2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        // Given
        Category category1 = new Category(
            new CategoryId(1L),
            new CategoryName("Category"),
            "Description",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        Category category2 = new Category(
            new CategoryId(2L),
            new CategoryName("Category"),
            "Description",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // When & Then
        assertNotEquals(category1, category2);
    }

    @Test
    void shouldNotBeEqualWhenOneIdIsNull() {
        // Given
        Category category1 = new Category(
            new CategoryName("Category"),
            "Description"
        );
        Category category2 = new Category(
            new CategoryId(1L),
            new CategoryName("Category"),
            "Description",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // When & Then
        assertNotEquals(category1, category2);
    }

    @Test
    void shouldHaveProperToStringRepresentation() {
        // Given
        CategoryId id = new CategoryId(1L);
        CategoryName name = new CategoryName("Electronics");
        String description = "Electronic devices";
        Category category = new Category(
            id,
            name,
            description,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // When
        String result = category.toString();

        // Then
        assertTrue(result.contains("Category{"));
        assertTrue(result.contains("id="));
        assertTrue(result.contains("name="));
        assertTrue(result.contains("Electronics"));
    }

    @Test
    void shouldIdentifyNewCategoryCorrectly() {
        // Given
        Category newCategory = new Category(
            new CategoryName("New Category"),
            "Description"
        );
        Category existingCategory = new Category(
            new CategoryId(1L),
            new CategoryName("Existing Category"),
            "Description",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // When & Then
        assertTrue(newCategory.isNew());
        assertFalse(existingCategory.isNew());
    }
}
