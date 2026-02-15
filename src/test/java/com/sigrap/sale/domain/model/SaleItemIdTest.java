package com.sigrap.sale.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SaleItemIdTest {

    @Test
    void shouldCreateSaleItemIdWithValidValue() {
        SaleItemId saleItemId = new SaleItemId(1L);
        assertEquals(1L, saleItemId.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new SaleItemId(null));
    }

    @Test
    void shouldThrowExceptionWhenValueIsZero() {
        assertThrows(IllegalArgumentException.class, () -> new SaleItemId(0L));
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new SaleItemId(-1L));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        SaleItemId saleItemId1 = new SaleItemId(1L);
        SaleItemId saleItemId2 = new SaleItemId(1L);
        assertEquals(saleItemId1, saleItemId2);
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        SaleItemId saleItemId1 = new SaleItemId(1L);
        SaleItemId saleItemId2 = new SaleItemId(2L);
        assertNotEquals(saleItemId1, saleItemId2);
    }
}
