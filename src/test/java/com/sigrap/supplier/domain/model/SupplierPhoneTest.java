package com.sigrap.supplier.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SupplierPhone value object.
 */
class SupplierPhoneTest {

    @Test
    void shouldCreateSupplierPhoneWithValidValue() {
        SupplierPhone phone = new SupplierPhone("123-456-7890");
        assertEquals("123-456-7890", phone.value());
    }

    @Test
    void shouldAcceptNullValue() {
        SupplierPhone phone = new SupplierPhone(null);
        assertNull(phone.value());
    }

    @Test
    void shouldThrowExceptionForTooLongValue() {
        String longPhone = "1".repeat(21);
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierPhone(longPhone)
        );
        assertEquals("Supplier phone cannot exceed 20 characters", exception.getMessage());
    }

    @Test
    void shouldAcceptMaximumLengthValue() {
        String maxPhone = "1".repeat(20);
        SupplierPhone phone = new SupplierPhone(maxPhone);
        assertEquals(maxPhone, phone.value());
    }

    @Test
    void shouldThrowExceptionForInvalidCharacters() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierPhone("123-456-7890abc")
        );
        assertEquals("Supplier phone contains invalid characters", exception.getMessage());
    }

    @Test
    void shouldAcceptPhoneWithParentheses() {
        SupplierPhone phone = new SupplierPhone("(123) 456-7890");
        assertEquals("(123) 456-7890", phone.value());
    }

    @Test
    void shouldAcceptPhoneWithPlus() {
        SupplierPhone phone = new SupplierPhone("+1-123-456-7890");
        assertEquals("+1-123-456-7890", phone.value());
    }

    @Test
    void shouldAcceptPhoneWithSpaces() {
        SupplierPhone phone = new SupplierPhone("123 456 7890");
        assertEquals("123 456 7890", phone.value());
    }

    @Test
    void shouldAcceptPhoneWithDashes() {
        SupplierPhone phone = new SupplierPhone("123-456-7890");
        assertEquals("123-456-7890", phone.value());
    }

    @Test
    void shouldBeEqualForSameValue() {
        SupplierPhone phone1 = new SupplierPhone("123-456-7890");
        SupplierPhone phone2 = new SupplierPhone("123-456-7890");
        assertEquals(phone1, phone2);
        assertEquals(phone1.hashCode(), phone2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        SupplierPhone phone1 = new SupplierPhone("123-456-7890");
        SupplierPhone phone2 = new SupplierPhone("098-765-4321");
        assertNotEquals(phone1, phone2);
    }
}
