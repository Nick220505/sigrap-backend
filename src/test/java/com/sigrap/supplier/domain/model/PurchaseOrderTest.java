package com.sigrap.supplier.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PurchaseOrder domain entity.
 */
class PurchaseOrderTest {

    @Test
    void shouldCreateNewPurchaseOrderWithRequiredFields() {
        PurchaseOrderNumber orderNumber = new PurchaseOrderNumber("PO-2024-001");
        SupplierId supplierId = new SupplierId(1L);
        LocalDate orderDate = LocalDate.now();
        BigDecimal totalAmount = new BigDecimal("1000.00");

        PurchaseOrder order = new PurchaseOrder(
            orderNumber, supplierId, orderDate, null, totalAmount, null
        );

        assertNull(order.getId());
        assertEquals(orderNumber, order.getOrderNumber());
        assertEquals(supplierId, order.getSupplierId());
        assertEquals(orderDate, order.getOrderDate());
        assertNull(order.getExpectedDeliveryDate());
        assertEquals(PurchaseOrderStatus.PENDING, order.getStatus());
        assertEquals(totalAmount, order.getTotalAmount());
        assertNull(order.getNotes());
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getUpdatedAt());
        assertTrue(order.isNew());
    }

    @Test
    void shouldCreatePurchaseOrderWithAllFields() {
        PurchaseOrderId id = new PurchaseOrderId(1L);
        PurchaseOrderNumber orderNumber = new PurchaseOrderNumber("PO-2024-001");
        SupplierId supplierId = new SupplierId(1L);
        LocalDate orderDate = LocalDate.now();
        LocalDate deliveryDate = LocalDate.now().plusDays(7);
        BigDecimal totalAmount = new BigDecimal("1000.00");
        String notes = "Test notes";
        LocalDateTime now = LocalDateTime.now();

        PurchaseOrder order = new PurchaseOrder(
            id, orderNumber, supplierId, orderDate, deliveryDate,
            PurchaseOrderStatus.APPROVED, totalAmount, notes, now, now
        );

        assertEquals(id, order.getId());
        assertEquals(orderNumber, order.getOrderNumber());
        assertEquals(supplierId, order.getSupplierId());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(deliveryDate, order.getExpectedDeliveryDate());
        assertEquals(PurchaseOrderStatus.APPROVED, order.getStatus());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(notes, order.getNotes());
        assertEquals(now, order.getCreatedAt());
        assertEquals(now, order.getUpdatedAt());
        assertFalse(order.isNew());
    }

    @Test
    void shouldThrowExceptionForNullOrderNumber() {
        SupplierId supplierId = new SupplierId(1L);
        LocalDate orderDate = LocalDate.now();
        BigDecimal totalAmount = new BigDecimal("1000.00");

        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new PurchaseOrder(null, supplierId, orderDate, null, totalAmount, null)
        );
        assertEquals("Order number cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullSupplierId() {
        PurchaseOrderNumber orderNumber = new PurchaseOrderNumber("PO-2024-001");
        LocalDate orderDate = LocalDate.now();
        BigDecimal totalAmount = new BigDecimal("1000.00");

        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new PurchaseOrder(orderNumber, null, orderDate, null, totalAmount, null)
        );
        assertEquals("Supplier ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullOrderDate() {
        PurchaseOrderNumber orderNumber = new PurchaseOrderNumber("PO-2024-001");
        SupplierId supplierId = new SupplierId(1L);
        BigDecimal totalAmount = new BigDecimal("1000.00");

        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new PurchaseOrder(orderNumber, supplierId, null, null, totalAmount, null)
        );
        assertEquals("Order date cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullTotalAmount() {
        PurchaseOrderNumber orderNumber = new PurchaseOrderNumber("PO-2024-001");
        SupplierId supplierId = new SupplierId(1L);
        LocalDate orderDate = LocalDate.now();

        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new PurchaseOrder(orderNumber, supplierId, orderDate, null, null, null)
        );
        assertEquals("Total amount cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNegativeTotalAmount() {
        PurchaseOrderNumber orderNumber = new PurchaseOrderNumber("PO-2024-001");
        SupplierId supplierId = new SupplierId(1L);
        LocalDate orderDate = LocalDate.now();
        BigDecimal negativeTotalAmount = new BigDecimal("-100.00");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new PurchaseOrder(orderNumber, supplierId, orderDate, null, negativeTotalAmount, null)
        );
        assertEquals("Total amount cannot be negative", exception.getMessage());
    }

    @Test
    void shouldAcceptZeroTotalAmount() {
        PurchaseOrderNumber orderNumber = new PurchaseOrderNumber("PO-2024-001");
        SupplierId supplierId = new SupplierId(1L);
        LocalDate orderDate = LocalDate.now();
        BigDecimal zeroAmount = BigDecimal.ZERO;

        PurchaseOrder order = new PurchaseOrder(
            orderNumber, supplierId, orderDate, null, zeroAmount, null
        );

        assertEquals(zeroAmount, order.getTotalAmount());
    }

    @Test
    void shouldApprovePendingOrder() {
        PurchaseOrder order = createTestPurchaseOrder();
        LocalDateTime oldUpdatedAt = order.getUpdatedAt();

        try { Thread.sleep(10); } catch (InterruptedException e) {}

        order.approve();

        assertEquals(PurchaseOrderStatus.APPROVED, order.getStatus());
        assertTrue(order.getUpdatedAt().isAfter(oldUpdatedAt));
    }

    @Test
    void shouldThrowExceptionWhenApprovingNonPendingOrder() {
        PurchaseOrder order = createTestPurchaseOrder();
        order.approve();

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> order.approve()
        );
        assertEquals("Can only approve pending purchase orders", exception.getMessage());
    }

    @Test
    void shouldReceiveApprovedOrder() {
        PurchaseOrder order = createTestPurchaseOrder();
        order.approve();
        LocalDateTime oldUpdatedAt = order.getUpdatedAt();

        try { Thread.sleep(10); } catch (InterruptedException e) {}

        order.receive();

        assertEquals(PurchaseOrderStatus.RECEIVED, order.getStatus());
        assertTrue(order.getUpdatedAt().isAfter(oldUpdatedAt));
    }

    @Test
    void shouldThrowExceptionWhenReceivingNonApprovedOrder() {
        PurchaseOrder order = createTestPurchaseOrder();

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> order.receive()
        );
        assertEquals("Can only receive approved purchase orders", exception.getMessage());
    }

    @Test
    void shouldCancelPendingOrder() {
        PurchaseOrder order = createTestPurchaseOrder();
        LocalDateTime oldUpdatedAt = order.getUpdatedAt();

        try { Thread.sleep(10); } catch (InterruptedException e) {}

        order.cancel();

        assertEquals(PurchaseOrderStatus.CANCELLED, order.getStatus());
        assertTrue(order.getUpdatedAt().isAfter(oldUpdatedAt));
    }

    @Test
    void shouldCancelApprovedOrder() {
        PurchaseOrder order = createTestPurchaseOrder();
        order.approve();

        order.cancel();

        assertEquals(PurchaseOrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenCancellingReceivedOrder() {
        PurchaseOrder order = createTestPurchaseOrder();
        order.approve();
        order.receive();

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> order.cancel()
        );
        assertEquals("Cannot cancel a received purchase order", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCancellingAlreadyCancelledOrder() {
        PurchaseOrder order = createTestPurchaseOrder();
        order.cancel();

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> order.cancel()
        );
        assertEquals("Purchase order is already cancelled", exception.getMessage());
    }

    @Test
    void shouldUpdateExpectedDeliveryDate() {
        PurchaseOrder order = createTestPurchaseOrder();
        LocalDate newDate = LocalDate.now().plusDays(10);
        LocalDateTime oldUpdatedAt = order.getUpdatedAt();

        try { Thread.sleep(10); } catch (InterruptedException e) {}

        order.updateExpectedDeliveryDate(newDate);

        assertEquals(newDate, order.getExpectedDeliveryDate());
        assertTrue(order.getUpdatedAt().isAfter(oldUpdatedAt));
    }

    @Test
    void shouldNotUpdateTimestampWhenDeliveryDateIsSame() {
        PurchaseOrder order = createTestPurchaseOrder();
        LocalDateTime oldUpdatedAt = order.getUpdatedAt();

        order.updateExpectedDeliveryDate(null);

        assertNull(order.getExpectedDeliveryDate());
        assertEquals(oldUpdatedAt, order.getUpdatedAt());
    }

    @Test
    void shouldUpdateNotes() {
        PurchaseOrder order = createTestPurchaseOrder();
        String newNotes = "Updated notes";
        LocalDateTime oldUpdatedAt = order.getUpdatedAt();

        try { Thread.sleep(10); } catch (InterruptedException e) {}

        order.updateNotes(newNotes);

        assertEquals(newNotes, order.getNotes());
        assertTrue(order.getUpdatedAt().isAfter(oldUpdatedAt));
    }

    @Test
    void shouldReturnTrueForCanBeModifiedWhenPending() {
        PurchaseOrder order = createTestPurchaseOrder();
        assertTrue(order.canBeModified());
    }

    @Test
    void shouldReturnTrueForCanBeModifiedWhenApproved() {
        PurchaseOrder order = createTestPurchaseOrder();
        order.approve();
        assertTrue(order.canBeModified());
    }

    @Test
    void shouldReturnFalseForCanBeModifiedWhenReceived() {
        PurchaseOrder order = createTestPurchaseOrder();
        order.approve();
        order.receive();
        assertFalse(order.canBeModified());
    }

    @Test
    void shouldReturnFalseForCanBeModifiedWhenCancelled() {
        PurchaseOrder order = createTestPurchaseOrder();
        order.cancel();
        assertFalse(order.canBeModified());
    }

    @Test
    void shouldBeEqualForSameId() {
        PurchaseOrderId id = new PurchaseOrderId(1L);
        PurchaseOrder order1 = new PurchaseOrder(
            id,
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.now(),
            null,
            PurchaseOrderStatus.PENDING,
            BigDecimal.TEN,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        PurchaseOrder order2 = new PurchaseOrder(
            id,
            new PurchaseOrderNumber("PO-002"),
            new SupplierId(2L),
            LocalDate.now(),
            null,
            PurchaseOrderStatus.APPROVED,
            BigDecimal.ONE,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentIds() {
        PurchaseOrder order1 = new PurchaseOrder(
            new PurchaseOrderId(1L),
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.now(),
            null,
            PurchaseOrderStatus.PENDING,
            BigDecimal.TEN,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        PurchaseOrder order2 = new PurchaseOrder(
            new PurchaseOrderId(2L),
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.now(),
            null,
            PurchaseOrderStatus.PENDING,
            BigDecimal.TEN,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        assertNotEquals(order1, order2);
    }

    @Test
    void shouldHaveToStringRepresentation() {
        PurchaseOrder order = createTestPurchaseOrder();
        String toString = order.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("PurchaseOrder"));
        assertTrue(toString.contains("PO-2024-001"));
    }

    private PurchaseOrder createTestPurchaseOrder() {
        return new PurchaseOrder(
            new PurchaseOrderNumber("PO-2024-001"),
            new SupplierId(1L),
            LocalDate.now(),
            null,
            new BigDecimal("1000.00"),
            null
        );
    }
}
