package com.sigrap.sale.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SaleIdTest {

    @Test
    void shouldCreateSaleIdWithValidValue() {
        SaleId saleId = new SaleId(1L);
        assertEquals(1L, saleId.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new SaleId(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {
        assertThrows(IllegalArgumentException.class, () -> new SaleId(0L));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new SaleId(-1L));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        SaleId saleId1 = new SaleId(1L);
        SaleId saleId2 = new SaleId(1L);
        assertEquals(saleId1, saleId2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        SaleId saleId1 = new SaleId(1L);
        SaleId saleId2 = new SaleId(2L);
        assertNotEquals(saleId1, saleId2);
    }

    @Test
    void shouldHaveSameHashCodeWhenValuesAreEqual() {
        SaleId saleId1 = new SaleId(1L);
        SaleId saleId2 = new SaleId(1L);
        assertEquals(saleId1.hashCode(), saleId2.hashCode());
    }
}
