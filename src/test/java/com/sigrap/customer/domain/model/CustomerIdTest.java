package com.sigrap.customer.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerIdTest {

    @Test
    void shouldCreateCustomerIdWithValidValue() {
        // Given
        Long value = 1L;

        // When
        CustomerId customerId = new CustomerId(value);

        // Then
        assertEquals(value, customerId.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerId(null)
        );
        assertEquals("Customer ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerId(0L)
        );
        assertEquals("Customer ID must be positive", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerId(-1L)
        );
        assertEquals("Customer ID must be positive", exception.getMessage());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        CustomerId id1 = new CustomerId(1L);
        CustomerId id2 = new CustomerId(1L);

        // Then
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        CustomerId id1 = new CustomerId(1L);
        CustomerId id2 = new CustomerId(2L);

        // Then
        assertNotEquals(id1, id2);
    }
}
