package com.sigrap.supplier.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SupplierId value object.
 */
class SupplierIdTest {

    @Test
    void shouldCreateSupplierIdWithValidValue() {
        SupplierId id = new SupplierId(1L);
        assertEquals(1L, id.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierId(null)
        );
        assertEquals("Supplier ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForZeroValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierId(0L)
        );
        assertEquals("Supplier ID must be positive", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNegativeValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new SupplierId(-1L)
        );
        assertEquals("Supplier ID must be positive", exception.getMessage());
    }

    @Test
    void shouldBeEqualForSameValue() {
        SupplierId id1 = new SupplierId(1L);
        SupplierId id2 = new SupplierId(1L);
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        SupplierId id1 = new SupplierId(1L);
        SupplierId id2 = new SupplierId(2L);
        assertNotEquals(id1, id2);
    }
}
