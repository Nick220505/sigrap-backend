package com.sigrap.product.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductName value object.
 * Pure unit tests with no Spring context.
 */
class ProductNameTest {

    @Test
    void shouldCreateProductNameWithValidValue() {
        // Given
        String validName = "Laptop Computer";

        // When
        ProductName productName = new ProductName(validName);

        // Then
        assertNotNull(productName);
        assertEquals(validName, productName.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ProductName(null)
        );
        assertEquals("Product name cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyString() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ProductName("")
        );
        assertEquals("Product name cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForBlankString() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ProductName("   ")
        );
        assertEquals("Product name cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNameExceeding255Characters() {
        // Given
        String longName = "a".repeat(256);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ProductName(longName)
        );
        assertEquals("Product name cannot exceed 255 characters", exception.getMessage());
    }

    @Test
    void shouldAcceptNameWith255Characters() {
        // Given
        String maxLengthName = "a".repeat(255);

        // When
        ProductName productName = new ProductName(maxLengthName);

        // Then
        assertNotNull(productName);
        assertEquals(maxLengthName, productName.value());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        ProductName name1 = new ProductName("Laptop");
        ProductName name2 = new ProductName("Laptop");

        // When & Then
        assertEquals(name1, name2);
        assertEquals(name1.hashCode(), name2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        ProductName name1 = new ProductName("Laptop");
        ProductName name2 = new ProductName("Desktop");

        // When & Then
        assertNotEquals(name1, name2);
    }

    @Test
    void shouldHaveProperToStringRepresentation() {
        // Given
        ProductName productName = new ProductName("Wireless Mouse");

        // When
        String result = productName.toString();

        // Then
        assertTrue(result.contains("Wireless Mouse"));
    }
}
