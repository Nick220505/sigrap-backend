package com.sigrap.category.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CategoryName value object.
 * Pure unit tests with no Spring context.
 */
class CategoryNameTest {

    @Test
    void shouldCreateCategoryNameWithValidValue() {
        // Given
        String validName = "Office Supplies";

        // When
        CategoryName categoryName = new CategoryName(validName);

        // Then
        assertNotNull(categoryName);
        assertEquals(validName, categoryName.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CategoryName(null)
        );
        assertEquals("Category name cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyString() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CategoryName("")
        );
        assertEquals("Category name cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForBlankString() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CategoryName("   ")
        );
        assertEquals("Category name cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNameExceeding100Characters() {
        // Given
        String longName = "a".repeat(101);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CategoryName(longName)
        );
        assertEquals("Category name cannot exceed 100 characters", exception.getMessage());
    }

    @Test
    void shouldAcceptNameWith100Characters() {
        // Given
        String maxLengthName = "a".repeat(100);

        // When
        CategoryName categoryName = new CategoryName(maxLengthName);

        // Then
        assertNotNull(categoryName);
        assertEquals(maxLengthName, categoryName.value());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        CategoryName name1 = new CategoryName("Electronics");
        CategoryName name2 = new CategoryName("Electronics");

        // When & Then
        assertEquals(name1, name2);
        assertEquals(name1.hashCode(), name2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        CategoryName name1 = new CategoryName("Electronics");
        CategoryName name2 = new CategoryName("Office Supplies");

        // When & Then
        assertNotEquals(name1, name2);
    }

    @Test
    void shouldHaveProperToStringRepresentation() {
        // Given
        CategoryName categoryName = new CategoryName("Books");

        // When
        String result = categoryName.toString();

        // Then
        assertTrue(result.contains("Books"));
    }

    @Test
    void shouldTrimWhitespaceIsNotApplied() {
        // Given - CategoryName does not trim, it validates as-is
        String nameWithSpaces = " Books ";

        // When
        CategoryName categoryName = new CategoryName(nameWithSpaces);

        // Then
        assertEquals(nameWithSpaces, categoryName.value());
    }
}
