package com.sigrap.product.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductPrice value object.
 * Pure unit tests with no Spring context.
 */
class ProductPriceTest {

    @Test
    void shouldCreateProductPriceWithValidValue() {
        // Given
        BigDecimal validPrice = new BigDecimal("99.99");

        // When
        ProductPrice productPrice = new ProductPrice(validPrice);

        // Then
        assertNotNull(productPrice);
        assertEquals(validPrice, productPrice.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ProductPrice(null)
        );
        assertEquals("Product price cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNegativeValue() {
        // Given
        BigDecimal negativePrice = new BigDecimal("-10.00");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ProductPrice(negativePrice)
        );
        assertEquals("Product price cannot be negative", exception.getMessage());
    }

    @Test
    void shouldAcceptZeroPrice() {
        // Given
        BigDecimal zeroPrice = BigDecimal.ZERO;

        // When
        ProductPrice productPrice = new ProductPrice(zeroPrice);

        // Then
        assertNotNull(productPrice);
        assertEquals(zeroPrice, productPrice.value());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        ProductPrice price1 = new ProductPrice(new BigDecimal("50.00"));
        ProductPrice price2 = new ProductPrice(new BigDecimal("50.00"));

        // When & Then
        assertEquals(price1, price2);
        assertEquals(price1.hashCode(), price2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        ProductPrice price1 = new ProductPrice(new BigDecimal("50.00"));
        ProductPrice price2 = new ProductPrice(new BigDecimal("60.00"));

        // When & Then
        assertNotEquals(price1, price2);
    }

    @Test
    void shouldReturnTrueWhenPriceIsGreaterThan() {
        // Given
        ProductPrice higherPrice = new ProductPrice(new BigDecimal("100.00"));
        ProductPrice lowerPrice = new ProductPrice(new BigDecimal("50.00"));

        // When
        boolean result = higherPrice.isGreaterThan(lowerPrice);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenPriceIsNotGreaterThan() {
        // Given
        ProductPrice lowerPrice = new ProductPrice(new BigDecimal("50.00"));
        ProductPrice higherPrice = new ProductPrice(new BigDecimal("100.00"));

        // When
        boolean result = lowerPrice.isGreaterThan(higherPrice);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenPricesAreEqual() {
        // Given
        ProductPrice price1 = new ProductPrice(new BigDecimal("50.00"));
        ProductPrice price2 = new ProductPrice(new BigDecimal("50.00"));

        // When
        boolean result = price1.isGreaterThan(price2);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldReturnTrueWhenPriceIsLessThan() {
        // Given
        ProductPrice lowerPrice = new ProductPrice(new BigDecimal("50.00"));
        ProductPrice higherPrice = new ProductPrice(new BigDecimal("100.00"));

        // When
        boolean result = lowerPrice.isLessThan(higherPrice);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenPriceIsNotLessThan() {
        // Given
        ProductPrice higherPrice = new ProductPrice(new BigDecimal("100.00"));
        ProductPrice lowerPrice = new ProductPrice(new BigDecimal("50.00"));

        // When
        boolean result = higherPrice.isLessThan(lowerPrice);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldThrowExceptionWhenComparingWithNull() {
        // Given
        ProductPrice price = new ProductPrice(new BigDecimal("50.00"));

        // When & Then
        assertThrows(NullPointerException.class, () -> price.isGreaterThan(null));
        assertThrows(NullPointerException.class, () -> price.isLessThan(null));
    }

    @Test
    void shouldHaveProperToStringRepresentation() {
        // Given
        ProductPrice productPrice = new ProductPrice(new BigDecimal("99.99"));

        // When
        String result = productPrice.toString();

        // Then
        assertTrue(result.contains("99.99"));
    }
}
