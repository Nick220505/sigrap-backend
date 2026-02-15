package com.sigrap.sale.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SaleNumberTest {

    @Test
    void shouldCreateSaleNumberWithValidValue() {
        SaleNumber saleNumber = new SaleNumber("SALE-2024-001");
        assertEquals("SALE-2024-001", saleNumber.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new SaleNumber(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new SaleNumber(""));
        assertThrows(IllegalArgumentException.class, () -> new SaleNumber("   "));
    }

    @Test
    void shouldThrowExceptionWhenValueExceedsMaxLength() {
        String longValue = "A".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> new SaleNumber(longValue));
    }

    @Test
    void shouldAcceptValueAtMaxLength() {
        String maxLengthValue = "A".repeat(50);
        SaleNumber saleNumber = new SaleNumber(maxLengthValue);
        assertEquals(maxLengthValue, saleNumber.value());
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        SaleNumber saleNumber1 = new SaleNumber("SALE-001");
        SaleNumber saleNumber2 = new SaleNumber("SALE-001");
        assertEquals(saleNumber1, saleNumber2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        SaleNumber saleNumber1 = new SaleNumber("SALE-001");
        SaleNumber saleNumber2 = new SaleNumber("SALE-002");
        assertNotEquals(saleNumber1, saleNumber2);
    }
}
