package com.sigrap.sale.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SaleReturnIdTest {

    @Test
    void shouldCreateSaleReturnIdWithValidValue() {
        SaleReturnId saleReturnId = new SaleReturnId(1L);
        assertEquals(1L, saleReturnId.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new SaleReturnId(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {
        assertThrows(IllegalArgumentException.class, () -> new SaleReturnId(0L));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new SaleReturnId(-1L));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        SaleReturnId saleReturnId1 = new SaleReturnId(1L);
        SaleReturnId saleReturnId2 = new SaleReturnId(1L);
        assertEquals(saleReturnId1, saleReturnId2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        SaleReturnId saleReturnId1 = new SaleReturnId(1L);
        SaleReturnId saleReturnId2 = new SaleReturnId(2L);
        assertNotEquals(saleReturnId1, saleReturnId2);
    }
}
