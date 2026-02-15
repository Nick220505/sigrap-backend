package com.sigrap.category.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CategoryId value object.
 * Pure unit tests with no Spring context.
 */
class CategoryIdTest {

    @Test
    void shouldCreateCategoryIdWithValidValue() {
        // Given
        Long validId = 1L;

        // When
        CategoryId categoryId = new CategoryId(validId);

        // Then
        assertNotNull(categoryId);
        assertEquals(validId, categoryId.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CategoryId(null)
        );
        assertEquals("Category ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForZeroValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CategoryId(0L)
        );
        assertEquals("Category ID must be positive", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNegativeValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CategoryId(-1L)
        );
        assertEquals("Category ID must be positive", exception.getMessage());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        CategoryId id1 = new CategoryId(1L);
        CategoryId id2 = new CategoryId(1L);

        // When & Then
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        CategoryId id1 = new CategoryId(1L);
        CategoryId id2 = new CategoryId(2L);

        // When & Then
        assertNotEquals(id1, id2);
    }

    @Test
    void shouldHaveProperToStringRepresentation() {
        // Given
        CategoryId categoryId = new CategoryId(42L);

        // When
        String result = categoryId.toString();

        // Then
        assertTrue(result.contains("42"));
    }
}
