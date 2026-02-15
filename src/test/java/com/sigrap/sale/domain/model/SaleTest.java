package com.sigrap.sale.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SaleTest {

    @Test
    void shouldCreateSaleWithValidData() {
        SaleNumber saleNumber = new SaleNumber("SALE-001");
        Long customerId = 1L;
        Long employeeId = 2L;
        LocalDateTime saleDate = LocalDateTime.now();
        PaymentMethod paymentMethod = PaymentMethod.CASH;

        Sale sale = new Sale(saleNumber, customerId, employeeId, saleDate, paymentMethod, "Test notes");

        assertNull(sale.getId());
        assertEquals(saleNumber, sale.getSaleNumber());
        assertEquals(customerId, sale.getCustomerId());
        assertEquals(employeeId, sale.getEmployeeId());
        assertEquals(saleDate, sale.getSaleDate());
        assertEquals(BigDecimal.ZERO, sale.getTotalAmount());
        assertEquals(paymentMethod, sale.getPaymentMethod());
        assertEquals(SaleStatus.PENDING, sale.getStatus());
        assertEquals("Test notes", sale.getNotes());
        assertTrue(sale.getItems().isEmpty());
    }

    @Test
    void shouldCreateSaleWithFullConstructor() {
        SaleId id = new SaleId(1L);
        SaleNumber saleNumber = new SaleNumber("SALE-001");
        Long customerId = 1L;
        Long employeeId = 2L;
        LocalDateTime saleDate = LocalDateTime.now();
        BigDecimal totalAmount = new BigDecimal("100.00");
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        SaleStatus status = SaleStatus.COMPLETED;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        Sale sale = new Sale(id, saleNumber, customerId, employeeId, saleDate,
                totalAmount, paymentMethod, status, "Notes", createdAt, updatedAt);

        assertEquals(id, sale.getId());
        assertEquals(saleNumber, sale.getSaleNumber());
        assertEquals(totalAmount, sale.getTotalAmount());
        assertEquals(status, sale.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenSaleNumberIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Sale(null, 1L, 2L, LocalDateTime.now(), PaymentMethod.CASH, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenCustomerIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Sale(new SaleNumber("SALE-001"), null, 2L, LocalDateTime.now(), PaymentMethod.CASH, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenEmployeeIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Sale(new SaleNumber("SALE-001"), 1L, null, LocalDateTime.now(), PaymentMethod.CASH, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSaleDateIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Sale(new SaleNumber("SALE-001"), 1L, 2L, null, PaymentMethod.CASH, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPaymentMethodIsNull() {
        assertThrows(NullPointerException.class, () ->
                new Sale(new SaleNumber("SALE-001"), 1L, 2L, LocalDateTime.now(), null, null)
        );
    }

    @Test
    void shouldAddItemToSale() {
        Sale sale = createTestSale();
        SaleItem item = createTestSaleItem(new SaleId(1L));

        sale.addItem(item);

        assertEquals(1, sale.getItems().size());
        assertEquals(new BigDecimal("50.00"), sale.getTotalAmount());
    }

    @Test
    void shouldAddMultipleItemsAndCalculateTotal() {
        Sale sale = createTestSale();
        SaleItem item1 = new SaleItem(new SaleId(1L), 100L, 5, new BigDecimal("10.00"));
        SaleItem item2 = new SaleItem(new SaleId(1L), 101L, 3, new BigDecimal("20.00"));

        sale.addItem(item1);
        sale.addItem(item2);

        assertEquals(2, sale.getItems().size());
        assertEquals(new BigDecimal("110.00"), sale.getTotalAmount());
    }

    @Test
    void shouldThrowExceptionWhenAddingNullItem() {
        Sale sale = createTestSale();
        assertThrows(NullPointerException.class, () -> sale.addItem(null));
    }

    @Test
    void shouldThrowExceptionWhenAddingItemToCompletedSale() {
        Sale sale = createCompletedSale();
        SaleItem item = createTestSaleItem(new SaleId(1L));

        assertThrows(IllegalStateException.class, () -> sale.addItem(item));
    }

    @Test
    void shouldThrowExceptionWhenAddingItemToCancelledSale() {
        Sale sale = createCancelledSale();
        SaleItem item = createTestSaleItem(new SaleId(1L));

        assertThrows(IllegalStateException.class, () -> sale.addItem(item));
    }

    @Test
    void shouldRemoveItemFromSale() {
        Sale sale = createTestSale();
        SaleItem item = createTestSaleItem(new SaleId(1L));
        sale.addItem(item);
        assertEquals(new BigDecimal("50.00"), sale.getTotalAmount());

        sale.removeItem(item);

        assertEquals(0, sale.getItems().size());
        assertEquals(BigDecimal.ZERO, sale.getTotalAmount());
    }

    @Test
    void shouldThrowExceptionWhenRemovingNullItem() {
        Sale sale = createTestSale();
        assertThrows(NullPointerException.class, () -> sale.removeItem(null));
    }

    @Test
    void shouldThrowExceptionWhenRemovingItemFromCompletedSale() {
        Sale sale = createCompletedSale();
        SaleItem item = createTestSaleItem(new SaleId(1L));

        assertThrows(IllegalStateException.class, () -> sale.removeItem(item));
    }

    @Test
    void shouldCalculateTotalCorrectly() {
        Sale sale = createTestSale();
        sale.addItem(new SaleItem(new SaleId(1L), 100L, 2, new BigDecimal("15.50")));
        sale.addItem(new SaleItem(new SaleId(1L), 101L, 5, new BigDecimal("8.00")));

        assertEquals(new BigDecimal("71.00"), sale.getTotalAmount());
    }

    @Test
    void shouldCompleteSale() {
        Sale sale = createTestSale();
        sale.addItem(createTestSaleItem(new SaleId(1L)));

        sale.complete();

        assertEquals(SaleStatus.COMPLETED, sale.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenCompletingAlreadyCompletedSale() {
        Sale sale = createCompletedSale();
        assertThrows(IllegalStateException.class, () -> sale.complete());
    }

    @Test
    void shouldThrowExceptionWhenCompletingCancelledSale() {
        Sale sale = createCancelledSale();
        assertThrows(IllegalStateException.class, () -> sale.complete());
    }

    @Test
    void shouldThrowExceptionWhenCompletingSaleWithNoItems() {
        Sale sale = createTestSale();
        assertThrows(IllegalStateException.class, () -> sale.complete());
    }

    @Test
    void shouldCancelSale() {
        Sale sale = createTestSale();

        sale.cancel();

        assertEquals(SaleStatus.CANCELLED, sale.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenCancellingCompletedSale() {
        Sale sale = createCompletedSale();
        assertThrows(IllegalStateException.class, () -> sale.cancel());
    }

    @Test
    void shouldThrowExceptionWhenCancellingAlreadyCancelledSale() {
        Sale sale = createCancelledSale();
        assertThrows(IllegalStateException.class, () -> sale.cancel());
    }

    @Test
    void shouldUpdatePaymentMethod() {
        Sale sale = createTestSale();

        sale.updatePaymentMethod(PaymentMethod.CREDIT_CARD);

        assertEquals(PaymentMethod.CREDIT_CARD, sale.getPaymentMethod());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPaymentMethodToNull() {
        Sale sale = createTestSale();
        assertThrows(NullPointerException.class, () -> sale.updatePaymentMethod(null));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPaymentMethodOfCompletedSale() {
        Sale sale = createCompletedSale();
        assertThrows(IllegalStateException.class, () ->
                sale.updatePaymentMethod(PaymentMethod.CREDIT_CARD)
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPaymentMethodOfCancelledSale() {
        Sale sale = createCancelledSale();
        assertThrows(IllegalStateException.class, () ->
                sale.updatePaymentMethod(PaymentMethod.CREDIT_CARD)
        );
    }

    @Test
    void shouldUpdateNotes() {
        Sale sale = createTestSale();

        sale.updateNotes("Updated notes");

        assertEquals("Updated notes", sale.getNotes());
    }

    @Test
    void shouldAllowNullNotes() {
        Sale sale = createTestSale();
        sale.updateNotes(null);
        assertNull(sale.getNotes());
    }

    @Test
    void shouldReturnTrueForIsNewWhenIdIsNull() {
        Sale sale = createTestSale();
        assertTrue(sale.isNew());
    }

    @Test
    void shouldReturnFalseForIsNewWhenIdIsPresent() {
        Sale sale = new Sale(
                new SaleId(1L),
                new SaleNumber("SALE-001"),
                1L,
                2L,
                LocalDateTime.now(),
                BigDecimal.ZERO,
                PaymentMethod.CASH,
                SaleStatus.PENDING,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        assertFalse(sale.isNew());
    }

    @Test
    void shouldReturnTrueForCanBeModifiedWhenStatusIsPending() {
        Sale sale = createTestSale();
        assertTrue(sale.canBeModified());
    }

    @Test
    void shouldReturnFalseForCanBeModifiedWhenStatusIsCompleted() {
        Sale sale = createCompletedSale();
        assertFalse(sale.canBeModified());
    }

    @Test
    void shouldReturnFalseForCanBeModifiedWhenStatusIsCancelled() {
        Sale sale = createCancelledSale();
        assertFalse(sale.canBeModified());
    }

    @Test
    void shouldReturnUnmodifiableListOfItems() {
        Sale sale = createTestSale();
        sale.addItem(createTestSaleItem(new SaleId(1L)));

        assertThrows(UnsupportedOperationException.class, () ->
                sale.getItems().add(createTestSaleItem(new SaleId(1L)))
        );
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        SaleId id = new SaleId(1L);
        Sale sale1 = new Sale(id, new SaleNumber("SALE-001"), 1L, 2L, LocalDateTime.now(),
                BigDecimal.ZERO, PaymentMethod.CASH, SaleStatus.PENDING, null,
                LocalDateTime.now(), LocalDateTime.now());
        Sale sale2 = new Sale(id, new SaleNumber("SALE-002"), 1L, 2L, LocalDateTime.now(),
                BigDecimal.ZERO, PaymentMethod.CASH, SaleStatus.PENDING, null,
                LocalDateTime.now(), LocalDateTime.now());
        assertEquals(sale1, sale2);
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        Sale sale1 = new Sale(new SaleId(1L), new SaleNumber("SALE-001"), 1L, 2L,
                LocalDateTime.now(), BigDecimal.ZERO, PaymentMethod.CASH, SaleStatus.PENDING,
                null, LocalDateTime.now(), LocalDateTime.now());
        Sale sale2 = new Sale(new SaleId(2L), new SaleNumber("SALE-001"), 1L, 2L,
                LocalDateTime.now(), BigDecimal.ZERO, PaymentMethod.CASH, SaleStatus.PENDING,
                null, LocalDateTime.now(), LocalDateTime.now());
        assertNotEquals(sale1, sale2);
    }

    @Test
    void shouldHaveValidToString() {
        Sale sale = createTestSale();
        String toString = sale.toString();
        assertTrue(toString.contains("Sale"));
        assertTrue(toString.contains("saleNumber="));
        assertTrue(toString.contains("status="));
    }

    // Helper methods

    private Sale createTestSale() {
        return new Sale(
                new SaleNumber("SALE-001"),
                1L,
                2L,
                LocalDateTime.now(),
                PaymentMethod.CASH,
                null
        );
    }

    private Sale createCompletedSale() {
        Sale sale = createTestSale();
        sale.addItem(createTestSaleItem(new SaleId(1L)));
        sale.complete();
        return sale;
    }

    private Sale createCancelledSale() {
        Sale sale = createTestSale();
        sale.cancel();
        return sale;
    }

    private SaleItem createTestSaleItem(SaleId saleId) {
        return new SaleItem(saleId, 100L, 5, new BigDecimal("10.00"));
    }
}
