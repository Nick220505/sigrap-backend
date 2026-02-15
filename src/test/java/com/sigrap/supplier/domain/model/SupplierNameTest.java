package com.sigrap.supplier.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SupplierName value object.
 */
class SupplierNameTest {

    @Test
    void shouldCreateSupplierNameWithValidValue() {
        SupplierName name = new SupplierName("ABC Supplies");
        assertEquals("ABC Supplies", name.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierName(null)
        );
        assertEquals("Supplier name cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForBlankValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierName("   ")
        );
        assertEquals("Supplier name cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierName("")
        );
        assertEquals("Supplier name cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForTooLongValue() {
        String longName = "A".repeat(101);
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierName(longName)
        );
        assertEquals("Supplier name cannot exceed 100 characters", exception.getMessage());
    }

    @Test
    void shouldAcceptMaximumLengthValue() {
        String maxName = "A".repeat(100);
        SupplierName name = new SupplierName(maxName);
        assertEquals(maxName, name.value());
    }

    @Test
    void shouldBeEqualForSameValue() {
        SupplierName name1 = new SupplierName("ABC Supplies");
        SupplierName name2 = new SupplierName("ABC Supplies");
        assertEquals(name1, name2);
        assertEquals(name1.hashCode(), name2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        SupplierName name1 = new SupplierName("ABC Supplies");
        SupplierName name2 = new SupplierName("XYZ Supplies");
        assertNotEquals(name1, name2);
    }
}
