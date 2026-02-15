package com.sigrap.customer.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerEmailTest {

    @Test
    void shouldCreateCustomerEmailWithValidValue() {
        // Given
        String value = "john.doe@example.com";

        // When
        CustomerEmail customerEmail = new CustomerEmail(value);

        // Then
        assertEquals(value, customerEmail.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerEmail(null)
        );
        assertEquals("Customer email cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerEmail("   ")
        );
        assertEquals("Customer email cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsEmpty() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerEmail("")
        );
        assertEquals("Customer email cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueExceedsMaxLength() {
        // Given
        String longEmail = "a".repeat(250) + "@test.com";

        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerEmail(longEmail)
        );
        assertEquals("Customer email cannot exceed 255 characters", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> new CustomerEmail("invalid-email"));
        assertThrows(IllegalArgumentException.class, () -> new CustomerEmail("@example.com"));
        assertThrows(IllegalArgumentException.class, () -> new CustomerEmail("user@"));
        assertThrows(IllegalArgumentException.class, () -> new CustomerEmail("user@.com"));
        assertThrows(IllegalArgumentException.class, () -> new CustomerEmail("user name@example.com"));
    }

    @Test
    void shouldAcceptValidEmailFormats() {
        // Valid email formats
        assertDoesNotThrow(() -> new CustomerEmail("user@example.com"));
        assertDoesNotThrow(() -> new CustomerEmail("user.name@example.com"));
        assertDoesNotThrow(() -> new CustomerEmail("user+tag@example.co.uk"));
        assertDoesNotThrow(() -> new CustomerEmail("user_name@example-domain.com"));
        assertDoesNotThrow(() -> new CustomerEmail("123@example.com"));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        CustomerEmail email1 = new CustomerEmail("john.doe@example.com");
        CustomerEmail email2 = new CustomerEmail("john.doe@example.com");

        // Then
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        CustomerEmail email1 = new CustomerEmail("john.doe@example.com");
        CustomerEmail email2 = new CustomerEmail("jane.smith@example.com");

        // Then
        assertNotEquals(email1, email2);
    }
}
