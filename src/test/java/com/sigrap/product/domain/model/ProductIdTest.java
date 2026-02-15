package com.sigrap.product.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductId value object.
 * Pure unit tests with no Spring context.
 */
class ProductIdTest {

    @Test
    void shouldCreateProductIdWithValidValue() {
        // Given
        Long validId = 1L;

        // When
        ProductId productId = new ProductId(validId);

        // Then
        assertNotNull(productId);
        assertEquals(validId, productId.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ProductId(null)
        );
        assertEquals("Product ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForZeroValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ProductId(0L)
        );
        assertEquals("Product ID must be positive", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNegativeValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ProductId(-1L)
        );
        assertEquals("Product ID must be positive", exception.getMessage());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        ProductId id1 = new ProductId(1L);
        ProductId id2 = new ProductId(1L);

        // When & Then
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        ProductId id1 = new ProductId(1L);
        ProductId id2 = new ProductId(2L);

        // When & Then
        assertNotEquals(id1, id2);
    }

    @Test
    void shouldHaveProperToStringRepresentation() {
        // Given
        ProductId productId = new ProductId(42L);

        // When
        String result = productId.toString();

        // Then
        assertTrue(result.contains("42"));
    }
}
