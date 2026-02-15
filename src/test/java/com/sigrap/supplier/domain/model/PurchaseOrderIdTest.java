package com.sigrap.supplier.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PurchaseOrderId value object.
 */
class PurchaseOrderIdTest {

    @Test
    void shouldCreatePurchaseOrderIdWithValidValue() {
        PurchaseOrderId id = new PurchaseOrderId(1L);
        assertEquals(1L, id.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new PurchaseOrderId(null)
        );
        assertEquals("Purchase order ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForZeroValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new PurchaseOrderId(0L)
        );
        assertEquals("Purchase order ID must be positive", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNegativeValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new PurchaseOrderId(-1L)
        );
        assertEquals("Purchase order ID must be positive", exception.getMessage());
    }

    @Test
    void shouldBeEqualForSameValue() {
        PurchaseOrderId id1 = new PurchaseOrderId(1L);
        PurchaseOrderId id2 = new PurchaseOrderId(1L);
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        PurchaseOrderId id1 = new PurchaseOrderId(1L);
        PurchaseOrderId id2 = new PurchaseOrderId(2L);
        assertNotEquals(id1, id2);
    }
}
