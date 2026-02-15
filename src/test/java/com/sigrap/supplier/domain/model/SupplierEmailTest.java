package com.sigrap.supplier.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SupplierEmail value object.
 */
class SupplierEmailTest {

    @Test
    void shouldCreateSupplierEmailWithValidValue() {
        SupplierEmail email = new SupplierEmail("supplier@example.com");
        assertEquals("supplier@example.com", email.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierEmail(null)
        );
        assertEquals("Supplier email cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForBlankValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierEmail("   ")
        );
        assertEquals("Supplier email cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierEmail("")
        );
        assertEquals("Supplier email cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidEmailFormat() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierEmail("invalid-email")
        );
        assertEquals("Supplier email must be a valid email address", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmailWithoutDomain() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierEmail("user@")
        );
        assertEquals("Supplier email must be a valid email address", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmailWithoutAt() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierEmail("userexample.com")
        );
        assertEquals("Supplier email must be a valid email address", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForTooLongValue() {
        String longEmail = "a".repeat(90) + "@example.com";
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierEmail(longEmail)
        );
        assertEquals("Supplier email cannot exceed 100 characters", exception.getMessage());
    }

    @Test
    void shouldAcceptValidEmailWithPlus() {
        SupplierEmail email = new SupplierEmail("user+tag@example.com");
        assertEquals("user+tag@example.com", email.value());
    }

    @Test
    void shouldAcceptValidEmailWithDot() {
        SupplierEmail email = new SupplierEmail("user.name@example.com");
        assertEquals("user.name@example.com", email.value());
    }

    @Test
    void shouldAcceptValidEmailWithUnderscore() {
        SupplierEmail email = new SupplierEmail("user_name@example.com");
        assertEquals("user_name@example.com", email.value());
    }

    @Test
    void shouldBeEqualForSameValue() {
        SupplierEmail email1 = new SupplierEmail("supplier@example.com");
        SupplierEmail email2 = new SupplierEmail("supplier@example.com");
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        SupplierEmail email1 = new SupplierEmail("supplier1@example.com");
        SupplierEmail email2 = new SupplierEmail("supplier2@example.com");
        assertNotEquals(email1, email2);
    }
}
