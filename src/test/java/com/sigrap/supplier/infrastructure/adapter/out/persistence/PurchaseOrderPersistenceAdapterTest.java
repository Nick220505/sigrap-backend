package com.sigrap.supplier.infrastructure.adapter.out.persistence;

import com.sigrap.supplier.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PurchaseOrderPersistenceAdapter.
 * Tests the adapter with a real database (H2) using Spring Boot test context.
 * 
 * These tests verify:
 * - Correct mapping between domain and JPA entities
 * - Database operations work as expected
 * - The adapter properly implements the repository port
 * - Status enum conversion works correctly
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PurchaseOrderPersistenceAdapterTest {

    @Autowired
    private PurchaseOrderPersistenceAdapter adapter;

    @Autowired
    private PurchaseOrderJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewPurchaseOrder() {
        // Given
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            new PurchaseOrderNumber("PO-2024-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            new BigDecimal("1500.00"),
            "Initial order for office supplies"
        );

        // When
        PurchaseOrder saved = adapter.save(purchaseOrder);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("PO-2024-001", saved.getOrderNumber().value());
        assertEquals(1L, saved.getSupplierId().value());
        assertEquals(LocalDate.of(2024, 1, 15), saved.getOrderDate());
        assertEquals(LocalDate.of(2024, 2, 15), saved.getExpectedDeliveryDate());
        assertEquals(0, new BigDecimal("1500.00").compareTo(saved.getTotalAmount()));
        assertEquals("Initial order for office supplies", saved.getNotes());
        assertEquals(PurchaseOrderStatus.PENDING, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldUpdateExistingPurchaseOrder() {
        // Given - save initial purchase order
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            new PurchaseOrderNumber("PO-2024-002"),
            new SupplierId(2L),
            LocalDate.of(2024, 1, 20),
            LocalDate.of(2024, 2, 20),
            new BigDecimal("2000.00"),
            "Original notes"
        );
        PurchaseOrder saved = adapter.save(purchaseOrder);
        
        // When - update the purchase order
        PurchaseOrder updated = new PurchaseOrder(
            saved.getId(),
            saved.getOrderNumber(),
            saved.getSupplierId(),
            saved.getOrderDate(),
            LocalDate.of(2024, 3, 1),
            PurchaseOrderStatus.APPROVED,
            saved.getTotalAmount(),
            "Updated notes",
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );
        PurchaseOrder result = adapter.save(updated);

        // Then
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals(LocalDate.of(2024, 3, 1), result.getExpectedDeliveryDate());
        assertEquals("Updated notes", result.getNotes());
        assertEquals(PurchaseOrderStatus.APPROVED, result.getStatus());
    }

    @Test
    void shouldFindPurchaseOrderById() {
        // Given
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            new PurchaseOrderNumber("PO-2024-003"),
            new SupplierId(3L),
            LocalDate.of(2024, 1, 25),
            LocalDate.of(2024, 2, 25),
            new BigDecimal("3000.00"),
            "Test order"
        );
        PurchaseOrder saved = adapter.save(purchaseOrder);

        // When
        Optional<PurchaseOrder> found = adapter.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("PO-2024-003", found.get().getOrderNumber().value());
        assertEquals(3L, found.get().getSupplierId().value());
    }

    @Test
    void shouldReturnEmptyWhenPurchaseOrderNotFound() {
        // Given
        PurchaseOrderId nonExistentId = new PurchaseOrderId(999L);

        // When
        Optional<PurchaseOrder> found = adapter.findById(nonExistentId);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllPurchaseOrders() {
        // Given
        adapter.save(new PurchaseOrder(
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 2, 1),
            new BigDecimal("1000.00"),
            "Order 1"
        ));
        adapter.save(new PurchaseOrder(
            new PurchaseOrderNumber("PO-002"),
            new SupplierId(2L),
            LocalDate.of(2024, 1, 2),
            LocalDate.of(2024, 2, 2),
            new BigDecimal("2000.00"),
            "Order 2"
        ));
        adapter.save(new PurchaseOrder(
            new PurchaseOrderNumber("PO-003"),
            new SupplierId(3L),
            LocalDate.of(2024, 1, 3),
            LocalDate.of(2024, 2, 3),
            new BigDecimal("3000.00"),
            null
        ));

        // When
        List<PurchaseOrder> orders = adapter.findAll();

        // Then
        assertNotNull(orders);
        assertEquals(3, orders.size());
        assertTrue(orders.stream().anyMatch(o -> o.getOrderNumber().value().equals("PO-001")));
        assertTrue(orders.stream().anyMatch(o -> o.getOrderNumber().value().equals("PO-002")));
        assertTrue(orders.stream().anyMatch(o -> o.getOrderNumber().value().equals("PO-003")));
    }

    @Test
    void shouldReturnEmptyListWhenNoPurchaseOrders() {
        // When
        List<PurchaseOrder> orders = adapter.findAll();

        // Then
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    void shouldFindPurchaseOrdersBySupplierId() {
        // Given
        SupplierId supplierId1 = new SupplierId(1L);
        SupplierId supplierId2 = new SupplierId(2L);
        
        adapter.save(new PurchaseOrder(
            new PurchaseOrderNumber("PO-S1-001"),
            supplierId1,
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 2, 1),
            new BigDecimal("1000.00"),
            "Supplier 1 order 1"
        ));
        adapter.save(new PurchaseOrder(
            new PurchaseOrderNumber("PO-S1-002"),
            supplierId1,
            LocalDate.of(2024, 1, 2),
            LocalDate.of(2024, 2, 2),
            new BigDecimal("1500.00"),
            "Supplier 1 order 2"
        ));
        adapter.save(new PurchaseOrder(
            new PurchaseOrderNumber("PO-S2-001"),
            supplierId2,
            LocalDate.of(2024, 1, 3),
            LocalDate.of(2024, 2, 3),
            new BigDecimal("2000.00"),
            "Supplier 2 order 1"
        ));

        // When
        List<PurchaseOrder> supplier1Orders = adapter.findBySupplierId(supplierId1);
        List<PurchaseOrder> supplier2Orders = adapter.findBySupplierId(supplierId2);

        // Then
        assertEquals(2, supplier1Orders.size());
        assertTrue(supplier1Orders.stream().allMatch(o -> o.getSupplierId().equals(supplierId1)));
        
        assertEquals(1, supplier2Orders.size());
        assertTrue(supplier2Orders.stream().allMatch(o -> o.getSupplierId().equals(supplierId2)));
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersForSupplier() {
        // Given
        SupplierId nonExistentSupplierId = new SupplierId(999L);

        // When
        List<PurchaseOrder> orders = adapter.findBySupplierId(nonExistentSupplierId);

        // Then
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    @Test
    void shouldFindPurchaseOrdersByStatus() {
        // Given
        PurchaseOrder pending1 = adapter.save(new PurchaseOrder(
            new PurchaseOrderNumber("PO-PENDING-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 2, 1),
            new BigDecimal("1000.00"),
            "Pending order 1"
        ));
        
        PurchaseOrder pending2 = adapter.save(new PurchaseOrder(
            new PurchaseOrderNumber("PO-PENDING-002"),
            new SupplierId(2L),
            LocalDate.of(2024, 1, 2),
            LocalDate.of(2024, 2, 2),
            new BigDecimal("1500.00"),
            "Pending order 2"
        ));
        
        // Approve one order
        PurchaseOrder approved = new PurchaseOrder(
            pending1.getId(),
            pending1.getOrderNumber(),
            pending1.getSupplierId(),
            pending1.getOrderDate(),
            pending1.getExpectedDeliveryDate(),
            PurchaseOrderStatus.APPROVED,
            pending1.getTotalAmount(),
            pending1.getNotes(),
            pending1.getCreatedAt(),
            pending1.getUpdatedAt()
        );
        adapter.save(approved);

        // When
        List<PurchaseOrder> pendingOrders = adapter.findByStatus(PurchaseOrderStatus.PENDING);
        List<PurchaseOrder> approvedOrders = adapter.findByStatus(PurchaseOrderStatus.APPROVED);

        // Then
        assertEquals(1, pendingOrders.size());
        assertTrue(pendingOrders.stream().allMatch(o -> o.getStatus() == PurchaseOrderStatus.PENDING));
        
        assertEquals(1, approvedOrders.size());
        assertTrue(approvedOrders.stream().allMatch(o -> o.getStatus() == PurchaseOrderStatus.APPROVED));
    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersWithStatus() {
        // Given - create only pending orders
        adapter.save(new PurchaseOrder(
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 2, 1),
            new BigDecimal("1000.00"),
            "Pending order"
        ));

        // When
        List<PurchaseOrder> receivedOrders = adapter.findByStatus(PurchaseOrderStatus.RECEIVED);

        // Then
        assertNotNull(receivedOrders);
        assertTrue(receivedOrders.isEmpty());
    }

    @Test
    void shouldDeletePurchaseOrderById() {
        // Given
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            new PurchaseOrderNumber("PO-TEMP"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 2, 1),
            new BigDecimal("1000.00"),
            "Temporary order"
        );
        PurchaseOrder saved = adapter.save(purchaseOrder);
        PurchaseOrderId savedId = saved.getId();

        // When
        adapter.deleteById(savedId);

        // Then
        Optional<PurchaseOrder> found = adapter.findById(savedId);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldPreserveTimestampsWhenSaving() {
        // Given
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            new PurchaseOrderNumber("PO-TEST"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 2, 1),
            new BigDecimal("1000.00"),
            "Test order"
        );

        // When
        PurchaseOrder saved = adapter.save(purchaseOrder);

        // Then
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldHandlePurchaseOrderWithNullNotes() {
        // Given
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            new PurchaseOrderNumber("PO-NO-NOTES"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 2, 1),
            new BigDecimal("1000.00"),
            null
        );

        // When
        PurchaseOrder saved = adapter.save(purchaseOrder);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("PO-NO-NOTES", saved.getOrderNumber().value());
        assertNull(saved.getNotes());
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        // Given
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            new PurchaseOrderNumber("PO-COMPLETE"),
            new SupplierId(5L),
            LocalDate.of(2024, 3, 15),
            LocalDate.of(2024, 4, 15),
            new BigDecimal("5000.50"),
            "Complete purchase order with all fields"
        );

        // When
        PurchaseOrder saved = adapter.save(purchaseOrder);
        Optional<PurchaseOrder> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        PurchaseOrder result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(saved.getOrderNumber().value(), result.getOrderNumber().value());
        assertEquals(saved.getSupplierId().value(), result.getSupplierId().value());
        assertEquals(saved.getOrderDate(), result.getOrderDate());
        assertEquals(saved.getExpectedDeliveryDate(), result.getExpectedDeliveryDate());
        assertEquals(saved.getTotalAmount(), result.getTotalAmount());
        assertEquals(saved.getNotes(), result.getNotes());
        assertEquals(saved.getStatus(), result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void shouldHandleAllStatusTransitions() {
        // Given
        PurchaseOrder purchaseOrder = adapter.save(new PurchaseOrder(
            new PurchaseOrderNumber("PO-STATUS-TEST"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 2, 1),
            new BigDecimal("1000.00"),
            "Status transition test"
        ));

        // Test PENDING -> APPROVED
        PurchaseOrder approved = new PurchaseOrder(
            purchaseOrder.getId(),
            purchaseOrder.getOrderNumber(),
            purchaseOrder.getSupplierId(),
            purchaseOrder.getOrderDate(),
            purchaseOrder.getExpectedDeliveryDate(),
            PurchaseOrderStatus.APPROVED,
            purchaseOrder.getTotalAmount(),
            purchaseOrder.getNotes(),
            purchaseOrder.getCreatedAt(),
            purchaseOrder.getUpdatedAt()
        );
        PurchaseOrder savedApproved = adapter.save(approved);
        assertEquals(PurchaseOrderStatus.APPROVED, savedApproved.getStatus());

        // Test APPROVED -> RECEIVED
        PurchaseOrder received = new PurchaseOrder(
            savedApproved.getId(),
            savedApproved.getOrderNumber(),
            savedApproved.getSupplierId(),
            savedApproved.getOrderDate(),
            savedApproved.getExpectedDeliveryDate(),
            PurchaseOrderStatus.RECEIVED,
            savedApproved.getTotalAmount(),
            savedApproved.getNotes(),
            savedApproved.getCreatedAt(),
            savedApproved.getUpdatedAt()
        );
        PurchaseOrder savedReceived = adapter.save(received);
        assertEquals(PurchaseOrderStatus.RECEIVED, savedReceived.getStatus());
    }

    @Test
    void shouldHandleCancelledStatus() {
        // Given
        PurchaseOrder purchaseOrder = adapter.save(new PurchaseOrder(
            new PurchaseOrderNumber("PO-CANCEL-TEST"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 2, 1),
            new BigDecimal("1000.00"),
            "To be cancelled"
        ));

        // When - cancel the order
        PurchaseOrder cancelled = new PurchaseOrder(
            purchaseOrder.getId(),
            purchaseOrder.getOrderNumber(),
            purchaseOrder.getSupplierId(),
            purchaseOrder.getOrderDate(),
            purchaseOrder.getExpectedDeliveryDate(),
            PurchaseOrderStatus.CANCELLED,
            purchaseOrder.getTotalAmount(),
            purchaseOrder.getNotes(),
            purchaseOrder.getCreatedAt(),
            purchaseOrder.getUpdatedAt()
        );
        PurchaseOrder savedCancelled = adapter.save(cancelled);

        // Then
        assertEquals(PurchaseOrderStatus.CANCELLED, savedCancelled.getStatus());
        
        // Verify we can find cancelled orders
        List<PurchaseOrder> cancelledOrders = adapter.findByStatus(PurchaseOrderStatus.CANCELLED);
        assertEquals(1, cancelledOrders.size());
        assertEquals(savedCancelled.getId(), cancelledOrders.get(0).getId());
    }

    @Test
    void shouldHandleLargeTotalAmounts() {
        // Given
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            new PurchaseOrderNumber("PO-LARGE"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2024, 2, 1),
            new BigDecimal("999999.99"),
            "Large amount order"
        );

        // When
        PurchaseOrder saved = adapter.save(purchaseOrder);
        Optional<PurchaseOrder> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(0, new BigDecimal("999999.99").compareTo(retrieved.get().getTotalAmount()));
    }

    @Test
    void shouldHandleFutureDates() {
        // Given
        LocalDate futureOrderDate = LocalDate.now().plusDays(30);
        LocalDate futureDeliveryDate = LocalDate.now().plusDays(60);
        
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            new PurchaseOrderNumber("PO-FUTURE"),
            new SupplierId(1L),
            futureOrderDate,
            futureDeliveryDate,
            new BigDecimal("1000.00"),
            "Future dated order"
        );

        // When
        PurchaseOrder saved = adapter.save(purchaseOrder);
        Optional<PurchaseOrder> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(futureOrderDate, retrieved.get().getOrderDate());
        assertEquals(futureDeliveryDate, retrieved.get().getExpectedDeliveryDate());
    }
}
