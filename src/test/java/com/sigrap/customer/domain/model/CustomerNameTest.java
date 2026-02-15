package com.sigrap.customer.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerNameTest {

    @Test
    void shouldCreateCustomerNameWithValidValue() {
        // Given
        String value = "John Doe";

        // When
        CustomerName customerName = new CustomerName(value);

        // Then
        assertEquals(value, customerName.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerName(null)
        );
        assertEquals("Customer name cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerName("   ")
        );
        assertEquals("Customer name cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsEmpty() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerName("")
        );
        assertEquals("Customer name cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueExceedsMaxLength() {
        // Given
        String longName = "a".repeat(256);

        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerName(longName)
        );
        assertEquals("Customer name cannot exceed 255 characters", exception.getMessage());
    }

    @Test
    void shouldAcceptNameAtMaxLength() {
        // Given
        String maxLengthName = "a".repeat(255);

        // When
        CustomerName customerName = new CustomerName(maxLengthName);

        // Then
        assertEquals(maxLengthName, customerName.value());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        CustomerName name1 = new CustomerName("John Doe");
        CustomerName name2 = new CustomerName("John Doe");

        // Then
        assertEquals(name1, name2);
        assertEquals(name1.hashCode(), name2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        CustomerName name1 = new CustomerName("John Doe");
        CustomerName name2 = new CustomerName("Jane Smith");

        // Then
        assertNotEquals(name1, name2);
    }
}
