package com.sigrap.sale.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SaleReturnNumberTest {

    @Test
    void shouldCreateSaleReturnNumberWithValidValue() {
        SaleReturnNumber returnNumber = new SaleReturnNumber("RET-2024-001");
        assertEquals("RET-2024-001", returnNumber.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new SaleReturnNumber(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new SaleReturnNumber(""));
        assertThrows(IllegalArgumentException.class, () -> new SaleReturnNumber("   "));
    }

    @Test
    void shouldThrowExceptionWhenValueExceedsMaxLength() {
        String longValue = "A".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> new SaleReturnNumber(longValue));
    }

    @Test
    void shouldAcceptValueAtMaxLength() {
        String maxLengthValue = "A".repeat(50);
        SaleReturnNumber returnNumber = new SaleReturnNumber(maxLengthValue);
        assertEquals(maxLengthValue, returnNumber.value());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        SaleReturnNumber returnNumber1 = new SaleReturnNumber("RET-001");
        SaleReturnNumber returnNumber2 = new SaleReturnNumber("RET-001");
        assertEquals(returnNumber1, returnNumber2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        SaleReturnNumber returnNumber1 = new SaleReturnNumber("RET-001");
        SaleReturnNumber returnNumber2 = new SaleReturnNumber("RET-002");
        assertNotEquals(returnNumber1, returnNumber2);
    }
}
