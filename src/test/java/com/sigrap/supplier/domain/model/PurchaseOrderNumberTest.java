package com.sigrap.supplier.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PurchaseOrderNumber value object.
 */
class PurchaseOrderNumberTest {

    @Test
    void shouldCreatePurchaseOrderNumberWithValidValue() {
        PurchaseOrderNumber number = new PurchaseOrderNumber("PO-2024-001");
        assertEquals("PO-2024-001", number.value());
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new PurchaseOrderNumber(null)
        );
        assertEquals("Purchase order number cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForBlankValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new PurchaseOrderNumber("   ")
        );
        assertEquals("Purchase order number cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyValue() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new PurchaseOrderNumber("")
        );
        assertEquals("Purchase order number cannot be blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForTooLongValue() {
        String longNumber = "A".repeat(51);
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new PurchaseOrderNumber(longNumber)
        );
        assertEquals("Purchase order number cannot exceed 50 characters", exception.getMessage());
    }

    @Test
    void shouldAcceptMaximumLengthValue() {
        String maxNumber = "A".repeat(50);
        PurchaseOrderNumber number = new PurchaseOrderNumber(maxNumber);
        assertEquals(maxNumber, number.value());
    }

    @Test
    void shouldBeEqualForSameValue() {
        PurchaseOrderNumber number1 = new PurchaseOrderNumber("PO-2024-001");
        PurchaseOrderNumber number2 = new PurchaseOrderNumber("PO-2024-001");
        assertEquals(number1, number2);
        assertEquals(number1.hashCode(), number2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        PurchaseOrderNumber number1 = new PurchaseOrderNumber("PO-2024-001");
        PurchaseOrderNumber number2 = new PurchaseOrderNumber("PO-2024-002");
        assertNotEquals(number1, number2);
    }
}
