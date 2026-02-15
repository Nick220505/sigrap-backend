package com.sigrap.customer.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerPhoneTest {

    @Test
    void shouldCreateCustomerPhoneWithValidValue() {
        // Given
        String value = "+1234567890";

        // When
        CustomerPhone customerPhone = new CustomerPhone(value);

        // Then
        assertEquals(value, customerPhone.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerPhone(null)
        );
        assertEquals("Customer phone cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerPhone("   ")
        );
        assertEquals("Customer phone cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueIsEmpty() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerPhone("")
        );
        assertEquals("Customer phone cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenValueExceedsMaxLength() {
        // Given
        String longPhone = "1".repeat(21);

        // When/Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CustomerPhone(longPhone)
        );
        assertEquals("Customer phone cannot exceed 20 characters", exception.getMessage());
    }

    @Test
    void shouldAcceptPhoneAtMaxLength() {
        // Given
        String maxLengthPhone = "1".repeat(20);

        // When
        CustomerPhone customerPhone = new CustomerPhone(maxLengthPhone);

        // Then
        assertEquals(maxLengthPhone, customerPhone.value());
    }

    @Test
    void shouldAcceptVariousPhoneFormats() {
        // Valid phone formats
        assertDoesNotThrow(() -> new CustomerPhone("+1234567890"));
        assertDoesNotThrow(() -> new CustomerPhone("123-456-7890"));
        assertDoesNotThrow(() -> new CustomerPhone("(123) 456-7890"));
        assertDoesNotThrow(() -> new CustomerPhone("1234567890"));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        // Given
        CustomerPhone phone1 = new CustomerPhone("+1234567890");
        CustomerPhone phone2 = new CustomerPhone("+1234567890");

        // Then
        assertEquals(phone1, phone2);
        assertEquals(phone1.hashCode(), phone2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        // Given
        CustomerPhone phone1 = new CustomerPhone("+1234567890");
        CustomerPhone phone2 = new CustomerPhone("+0987654321");

        // Then
        assertNotEquals(phone1, phone2);
    }
}
