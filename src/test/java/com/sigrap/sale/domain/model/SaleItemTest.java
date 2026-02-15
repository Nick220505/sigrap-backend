package com.sigrap.sale.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SaleItemTest {

    @Test
    void shouldCreateSaleItemWithValidData() {
        SaleId saleId = new SaleId(1L);
        Long productId = 100L;
        int quantity = 5;
        BigDecimal unitPrice = new BigDecimal("10.00");

        SaleItem saleItem = new SaleItem(saleId, productId, quantity, unitPrice);

        assertNull(saleItem.getId());
        assertEquals(saleId, saleItem.getSaleId());
        assertEquals(productId, saleItem.getProductId());
        assertEquals(quantity, saleItem.getQuantity());
        assertEquals(unitPrice, saleItem.getUnitPrice());
        assertEquals(new BigDecimal("50.00"), saleItem.getSubtotal());
    }

    @Test
    void shouldCreateSaleItemWithId() {
        SaleItemId id = new SaleItemId(1L);
        SaleId saleId = new SaleId(1L);
        Long productId = 100L;
        int quantity = 3;
        BigDecimal unitPrice = new BigDecimal("15.50");

        SaleItem saleItem = new SaleItem(id, saleId, productId, quantity, unitPrice);

        assertEquals(id, saleItem.getId());
        assertEquals(saleId, saleItem.getSaleId());
        assertEquals(productId, saleItem.getProductId());
        assertEquals(quantity, saleItem.getQuantity());
        assertEquals(unitPrice, saleItem.getUnitPrice());
        assertEquals(new BigDecimal("46.50"), saleItem.getSubtotal());
    }

    @Test
    void shouldThrowExceptionWhenSaleIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                new SaleItem(null, 100L, 5, new BigDecimal("10.00"))
        );
    }

    @Test
    void shouldThrowExceptionWhenProductIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                new SaleItem(new SaleId(1L), null, 5, new BigDecimal("10.00"))
        );
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsZero() {
        assertThrows(IllegalArgumentException.class, () ->
                new SaleItem(new SaleId(1L), 100L, 0, new BigDecimal("10.00"))
        );
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new SaleItem(new SaleId(1L), 100L, -1, new BigDecimal("10.00"))
        );
    }

    @Test
    void shouldThrowExceptionWhenUnitPriceIsNull() {
        assertThrows(NullPointerException.class, () ->
                new SaleItem(new SaleId(1L), 100L, 5, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenUnitPriceIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new SaleItem(new SaleId(1L), 100L, 5, new BigDecimal("-10.00"))
        );
    }

    @Test
    void shouldAcceptZeroUnitPrice() {
        SaleItem saleItem = new SaleItem(new SaleId(1L), 100L, 5, BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, saleItem.getUnitPrice());
        assertEquals(BigDecimal.ZERO, saleItem.getSubtotal());
    }

    @Test
    void shouldCalculateSubtotalCorrectly() {
        SaleItem saleItem = new SaleItem(new SaleId(1L), 100L, 10, new BigDecimal("25.50"));
        assertEquals(new BigDecimal("255.00"), saleItem.getSubtotal());
    }

    @Test
    void shouldUpdateQuantity() {
        SaleItem saleItem = new SaleItem(new SaleId(1L), 100L, 5, new BigDecimal("10.00"));
        assertEquals(new BigDecimal("50.00"), saleItem.getSubtotal());

        saleItem.updateQuantity(10);

        assertEquals(10, saleItem.getQuantity());
        assertEquals(new BigDecimal("100.00"), saleItem.getSubtotal());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingQuantityToZero() {
        SaleItem saleItem = new SaleItem(new SaleId(1L), 100L, 5, new BigDecimal("10.00"));
        assertThrows(IllegalArgumentException.class, () -> saleItem.updateQuantity(0));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingQuantityToNegative() {
        SaleItem saleItem = new SaleItem(new SaleId(1L), 100L, 5, new BigDecimal("10.00"));
        assertThrows(IllegalArgumentException.class, () -> saleItem.updateQuantity(-1));
    }

    @Test
    void shouldUpdateUnitPrice() {
        SaleItem saleItem = new SaleItem(new SaleId(1L), 100L, 5, new BigDecimal("10.00"));
        assertEquals(new BigDecimal("50.00"), saleItem.getSubtotal());

        saleItem.updateUnitPrice(new BigDecimal("15.00"));

        assertEquals(new BigDecimal("15.00"), saleItem.getUnitPrice());
        assertEquals(new BigDecimal("75.00"), saleItem.getSubtotal());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUnitPriceToNull() {
        SaleItem saleItem = new SaleItem(new SaleId(1L), 100L, 5, new BigDecimal("10.00"));
        assertThrows(NullPointerException.class, () -> saleItem.updateUnitPrice(null));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUnitPriceToNegative() {
        SaleItem saleItem = new SaleItem(new SaleId(1L), 100L, 5, new BigDecimal("10.00"));
        assertThrows(IllegalArgumentException.class, () ->
                saleItem.updateUnitPrice(new BigDecimal("-5.00"))
        );
    }

    @Test
    void shouldReturnTrueForIsNewWhenIdIsNull() {
        SaleItem saleItem = new SaleItem(new SaleId(1L), 100L, 5, new BigDecimal("10.00"));
        assertTrue(saleItem.isNew());
    }

    @Test
    void shouldReturnFalseForIsNewWhenIdIsPresent() {
        SaleItem saleItem = new SaleItem(
                new SaleItemId(1L),
                new SaleId(1L),
                100L,
                5,
                new BigDecimal("10.00")
        );
        assertFalse(saleItem.isNew());
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        SaleItemId id = new SaleItemId(1L);
        SaleItem saleItem1 = new SaleItem(id, new SaleId(1L), 100L, 5, new BigDecimal("10.00"));
        SaleItem saleItem2 = new SaleItem(id, new SaleId(1L), 100L, 5, new BigDecimal("10.00"));
        assertEquals(saleItem1, saleItem2);
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        SaleItem saleItem1 = new SaleItem(
                new SaleItemId(1L),
                new SaleId(1L),
                100L,
                5,
                new BigDecimal("10.00")
        );
        SaleItem saleItem2 = new SaleItem(
                new SaleItemId(2L),
                new SaleId(1L),
                100L,
                5,
                new BigDecimal("10.00")
        );
        assertNotEquals(saleItem1, saleItem2);
    }

    @Test
    void shouldHaveValidToString() {
        SaleItem saleItem = new SaleItem(
                new SaleItemId(1L),
                new SaleId(1L),
                100L,
                5,
                new BigDecimal("10.00")
        );
        String toString = saleItem.toString();
        assertTrue(toString.contains("SaleItem"));
        assertTrue(toString.contains("id="));
        assertTrue(toString.contains("saleId="));
    }
}
