package com.sigrap.product.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductStock value object.
 * Pure unit tests with no Spring context.
 */
class ProductStockTest {

    @Test
    void shouldCreateProductStockWithValidValue() {
        // Given
        Integer validStock = 100;

        // When
        ProductStock productStock = new ProductStock(validStock);

        // Then
        assertNotNull(productStock);
        assertEquals(validStock, productStock.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ProductStock(null)
        );
        assertEquals("Product stock cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNegativeValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ProductStock(-1)
        );
        assertEquals("Product stock cannot be negative", exception.getMessage());
    }

    @Test
    void shouldAcceptZeroStock() {
        // Given
        Integer zeroStock = 0;

        // When
        ProductStock productStock = new ProductStock(zeroStock);

        // Then
        assertNotNull(productStock);
        assertEquals(zeroStock, productStock.value());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        ProductStock stock1 = new ProductStock(50);
        ProductStock stock2 = new ProductStock(50);

        // When & Then
        assertEquals(stock1, stock2);
        assertEquals(stock1.hashCode(), stock2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        ProductStock stock1 = new ProductStock(50);
        ProductStock stock2 = new ProductStock(60);

        // When & Then
        assertNotEquals(stock1, stock2);
    }

    @Test
    void shouldReturnTrueWhenStockIsBelowThreshold() {
        // Given
        ProductStock stock = new ProductStock(5);
        ProductStock threshold = new ProductStock(10);

        // When
        boolean result = stock.isBelowThreshold(threshold);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenStockIsAboveThreshold() {
        // Given
        ProductStock stock = new ProductStock(15);
        ProductStock threshold = new ProductStock(10);

        // When
        boolean result = stock.isBelowThreshold(threshold);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenStockEqualsThreshold() {
        // Given
        ProductStock stock = new ProductStock(10);
        ProductStock threshold = new ProductStock(10);

        // When
        boolean result = stock.isBelowThreshold(threshold);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenThresholdIsNull() {
        // Given
        ProductStock stock = new ProductStock(5);

        // When
        boolean result = stock.isBelowThreshold(null);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldAddQuantitySuccessfully() {
        // Given
        ProductStock stock = new ProductStock(50);
        Integer quantityToAdd = 30;

        // When
        ProductStock newStock = stock.add(quantityToAdd);

        // Then
        assertEquals(80, newStock.value());
        assertEquals(50, stock.value()); // Original unchanged
    }

    @Test
    void shouldThrowExceptionWhenAddingNullQuantity() {
        // Given
        ProductStock stock = new ProductStock(50);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> stock.add(null)
        );
        assertEquals("Quantity to add must be non-negative", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAddingNegativeQuantity() {
        // Given
        ProductStock stock = new ProductStock(50);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> stock.add(-10)
        );
        assertEquals("Quantity to add must be non-negative", exception.getMessage());
    }

    @Test
    void shouldSubtractQuantitySuccessfully() {
        // Given
        ProductStock stock = new ProductStock(50);
        Integer quantityToSubtract = 20;

        // When
        ProductStock newStock = stock.subtract(quantityToSubtract);

        // Then
        assertEquals(30, newStock.value());
        assertEquals(50, stock.value()); // Original unchanged
    }

    @Test
    void shouldThrowExceptionWhenSubtractingNullQuantity() {
        // Given
        ProductStock stock = new ProductStock(50);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> stock.subtract(null)
        );
        assertEquals("Quantity to subtract must be non-negative", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSubtractingNegativeQuantity() {
        // Given
        ProductStock stock = new ProductStock(50);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> stock.subtract(-10)
        );
        assertEquals("Quantity to subtract must be non-negative", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSubtractingMoreThanAvailable() {
        // Given
        ProductStock stock = new ProductStock(50);
        Integer quantityToSubtract = 60;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> stock.subtract(quantityToSubtract)
        );
        assertEquals("Cannot subtract 60 from stock of 50", exception.getMessage());
    }

    @Test
    void shouldSubtractAllStock() {
        // Given
        ProductStock stock = new ProductStock(50);

        // When
        ProductStock newStock = stock.subtract(50);

        // Then
        assertEquals(0, newStock.value());
    }

    @Test
    void shouldHaveProperToStringRepresentation() {
        // Given
        ProductStock productStock = new ProductStock(100);

        // When
        String result = productStock.toString();

        // Then
        assertTrue(result.contains("100"));
    }
}
