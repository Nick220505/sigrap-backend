package com.sigrap.sale.infrastructure.adapter.out.persistence;

import com.sigrap.sale.domain.model.Sale;
import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleNumber;
import com.sigrap.sale.domain.model.SaleStatus;
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
 * Integration tests for SalePersistenceAdapter.
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
class SalePersistenceAdapterTest {

    @Autowired
    private SalePersistenceAdapter adapter;

    @Autowired
    private SaleJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewSale() {
        // Given
        Sale sale = new Sale(
            new SaleNumber("SALE-2024-001"),
            1L,
            1L,
            LocalDate.of(2024, 1, 15),
            "CASH",
            "Test sale"
        );

        // When
        Sale saved = adapter.save(sale);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("SALE-2024-001", saved.getSaleNumber().value());
        assertEquals(1L, saved.getCustomerId());
        assertEquals(1L, saved.getEmployeeId());
        assertEquals(LocalDate.of(2024, 1, 15), saved.getSaleDate());
        assertEquals("CASH", saved.getPaymentMethod());
        assertEquals("Test sale", saved.getNotes());
        assertEquals(SaleStatus.PENDING, saved.getStatus());
        assertEquals(0, BigDecimal.ZERO.compareTo(saved.getTotalAmount()));
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldUpdateExistingSale() {
        // Given - save initial sale
        Sale sale = new Sale(
            new SaleNumber("SALE-2024-002"),
            2L,
            2L,
            LocalDate.of(2024, 1, 20),
            "CREDIT_CARD",
            "Original notes"
        );
        Sale saved = adapter.save(sale);
        
        // When - update the sale
        Sale updated = new Sale(
            saved.getId(),
            saved.getSaleNumber(),
            saved.getCustomerId(),
            saved.getEmployeeId(),
            saved.getSaleDate(),
            SaleStatus.COMPLETED,
            "DEBIT_CARD",
            new BigDecimal("150.00"),
            "Updated notes",
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );
        Sale result = adapter.save(updated);

        // Then
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("DEBIT_CARD", result.getPaymentMethod());
        assertEquals("Updated notes", result.getNotes());
        assertEquals(SaleStatus.COMPLETED, result.getStatus());
        assertEquals(0, new BigDecimal("150.00").compareTo(result.getTotalAmount()));
    }

    @Test
    void shouldFindSaleById() {
        // Given
        Sale sale = new Sale(
            new SaleNumber("SALE-2024-003"),
            3L,
            3L,
            LocalDate.of(2024, 1, 25),
            "CASH",
            "Test sale"
        );
        Sale saved = adapter.save(sale);

        // When
        Optional<Sale> found = adapter.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("SALE-2024-003", found.get().getSaleNumber().value());
        assertEquals(3L, found.get().getCustomerId());
    }

    @Test
    void shouldReturnEmptyWhenSaleNotFound() {
        // Given
        SaleId nonExistentId = new SaleId(999L);

        // When
        Optional<Sale> found = adapter.findById(nonExistentId);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllSales() {
        // Given
        adapter.save(new Sale(
            new SaleNumber("SALE-001"),
            1L,
            1L,
            LocalDate.of(2024, 1, 1),
            "CASH",
            "Sale 1"
        ));
        adapter.save(new Sale(
            new SaleNumber("SALE-002"),
            2L,
            2L,
            LocalDate.of(2024, 1, 2),
            "CREDIT_CARD",
            "Sale 2"
        ));
        adapter.save(new Sale(
            new SaleNumber("SALE-003"),
            3L,
            3L,
            LocalDate.of(2024, 1, 3),
            "DEBIT_CARD",
            null
        ));

        // When
        List<Sale> sales = adapter.findAll();

        // Then
        assertNotNull(sales);
        assertEquals(3, sales.size());
        assertTrue(sales.stream().anyMatch(s -> s.getSaleNumber().value().equals("SALE-001")));
        assertTrue(sales.stream().anyMatch(s -> s.getSaleNumber().value().equals("SALE-002")));
        assertTrue(sales.stream().anyMatch(s -> s.getSaleNumber().value().equals("SALE-003")));
    }

    @Test
    void shouldReturnEmptyListWhenNoSales() {
        // When
        List<Sale> sales = adapter.findAll();

        // Then
        assertNotNull(sales);
        assertTrue(sales.isEmpty());
    }

    @Test
    void shouldFindSalesByCustomerId() {
        // Given
        Long customerId1 = 1L;
        Long customerId2 = 2L;
        
        adapter.save(new Sale(
            new SaleNumber("SALE-C1-001"),
            customerId1,
            1L,
            LocalDate.of(2024, 1, 1),
            "CASH",
            "Customer 1 sale 1"
        ));
        adapter.save(new Sale(
            new SaleNumber("SALE-C1-002"),
            customerId1,
            1L,
            LocalDate.of(2024, 1, 2),
            "CREDIT_CARD",
            "Customer 1 sale 2"
        ));
        adapter.save(new Sale(
            new SaleNumber("SALE-C2-001"),
            customerId2,
            2L,
            LocalDate.of(2024, 1, 3),
            "CASH",
            "Customer 2 sale 1"
        ));

        // When
        List<Sale> customer1Sales = adapter.findByCustomerId(customerId1);
        List<Sale> customer2Sales = adapter.findByCustomerId(customerId2);

        // Then
        assertEquals(2, customer1Sales.size());
        assertTrue(customer1Sales.stream().allMatch(s -> s.getCustomerId().equals(customerId1)));
        
        assertEquals(1, customer2Sales.size());
        assertTrue(customer2Sales.stream().allMatch(s -> s.getCustomerId().equals(customerId2)));
    }

    @Test
    void shouldReturnEmptyListWhenNoSalesForCustomer() {
        // Given
        Long nonExistentCustomerId = 999L;

        // When
        List<Sale> sales = adapter.findByCustomerId(nonExistentCustomerId);

        // Then
        assertNotNull(sales);
        assertTrue(sales.isEmpty());
    }

    @Test
    void shouldFindSalesByStatus() {
        // Given
        Sale pending1 = adapter.save(new Sale(
            new SaleNumber("SALE-PENDING-001"),
            1L,
            1L,
            LocalDate.of(2024, 1, 1),
            "CASH",
            "Pending sale 1"
        ));
        
        Sale pending2 = adapter.save(new Sale(
            new SaleNumber("SALE-PENDING-002"),
            2L,
            2L,
            LocalDate.of(2024, 1, 2),
            "CREDIT_CARD",
            "Pending sale 2"
        ));
        
        // Complete one sale
        Sale completed = new Sale(
            pending1.getId(),
            pending1.getSaleNumber(),
            pending1.getCustomerId(),
            pending1.getEmployeeId(),
            pending1.getSaleDate(),
            SaleStatus.COMPLETED,
            pending1.getPaymentMethod(),
            pending1.getTotalAmount(),
            pending1.getNotes(),
            pending1.getCreatedAt(),
            pending1.getUpdatedAt()
        );
        adapter.save(completed);

        // When
        List<Sale> pendingSales = adapter.findByStatus(SaleStatus.PENDING);
        List<Sale> completedSales = adapter.findByStatus(SaleStatus.COMPLETED);

        // Then
        assertEquals(1, pendingSales.size());
        assertTrue(pendingSales.stream().allMatch(s -> s.getStatus() == SaleStatus.PENDING));
        
        assertEquals(1, completedSales.size());
        assertTrue(completedSales.stream().allMatch(s -> s.getStatus() == SaleStatus.COMPLETED));
    }

    @Test
    void shouldReturnEmptyListWhenNoSalesWithStatus() {
        // Given - create only pending sales
        adapter.save(new Sale(
            new SaleNumber("SALE-001"),
            1L,
            1L,
            LocalDate.of(2024, 1, 1),
            "CASH",
            "Pending sale"
        ));

        // When
        List<Sale> cancelledSales = adapter.findByStatus(SaleStatus.CANCELLED);

        // Then
        assertNotNull(cancelledSales);
        assertTrue(cancelledSales.isEmpty());
    }

    @Test
    void shouldDeleteSaleById() {
        // Given
        Sale sale = new Sale(
            new SaleNumber("SALE-TEMP"),
            1L,
            1L,
            LocalDate.of(2024, 1, 1),
            "CASH",
            "Temporary sale"
        );
        Sale saved = adapter.save(sale);
        SaleId savedId = saved.getId();

        // When
        adapter.deleteById(savedId);

        // Then
        Optional<Sale> found = adapter.findById(savedId);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldPreserveTimestampsWhenSaving() {
        // Given
        Sale sale = new Sale(
            new SaleNumber("SALE-TEST"),
            1L,
            1L,
            LocalDate.of(2024, 1, 1),
            "CASH",
            "Test sale"
        );

        // When
        Sale saved = adapter.save(sale);

        // Then
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldHandleSaleWithNullNotes() {
        // Given
        Sale sale = new Sale(
            new SaleNumber("SALE-NO-NOTES"),
            1L,
            1L,
            LocalDate.of(2024, 1, 1),
            "CASH",
            null
        );

        // When
        Sale saved = adapter.save(sale);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("SALE-NO-NOTES", saved.getSaleNumber().value());
        assertNull(saved.getNotes());
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        // Given
        Sale sale = new Sale(
            new SaleNumber("SALE-COMPLETE"),
            5L,
            5L,
            LocalDate.of(2024, 3, 15),
            "CREDIT_CARD",
            "Complete sale with all fields"
        );

        // When
        Sale saved = adapter.save(sale);
        Optional<Sale> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        Sale result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(saved.getSaleNumber().value(), result.getSaleNumber().value());
        assertEquals(saved.getCustomerId(), result.getCustomerId());
        assertEquals(saved.getEmployeeId(), result.getEmployeeId());
        assertEquals(saved.getSaleDate(), result.getSaleDate());
        assertEquals(saved.getPaymentMethod(), result.getPaymentMethod());
        assertEquals(saved.getTotalAmount(), result.getTotalAmount());
        assertEquals(saved.getNotes(), result.getNotes());
        assertEquals(saved.getStatus(), result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void shouldHandleAllStatusTransitions() {
        // Given
        Sale sale = adapter.save(new Sale(
            new SaleNumber("SALE-STATUS-TEST"),
            1L,
            1L,
            LocalDate.of(2024, 1, 1),
            "CASH",
            "Status transition test"
        ));

        // Test PENDING -> COMPLETED
        Sale completed = new Sale(
            sale.getId(),
            sale.getSaleNumber(),
            sale.getCustomerId(),
            sale.getEmployeeId(),
            sale.getSaleDate(),
            SaleStatus.COMPLETED,
            sale.getPaymentMethod(),
            sale.getTotalAmount(),
            sale.getNotes(),
            sale.getCreatedAt(),
            sale.getUpdatedAt()
        );
        Sale savedCompleted = adapter.save(completed);
        assertEquals(SaleStatus.COMPLETED, savedCompleted.getStatus());

        // Test COMPLETED -> CANCELLED (should be allowed for testing)
        Sale cancelled = new Sale(
            savedCompleted.getId(),
            savedCompleted.getSaleNumber(),
            savedCompleted.getCustomerId(),
            savedCompleted.getEmployeeId(),
            savedCompleted.getSaleDate(),
            SaleStatus.CANCELLED,
            savedCompleted.getPaymentMethod(),
            savedCompleted.getTotalAmount(),
            savedCompleted.getNotes(),
            savedCompleted.getCreatedAt(),
            savedCompleted.getUpdatedAt()
        );
        Sale savedCancelled = adapter.save(cancelled);
        assertEquals(SaleStatus.CANCELLED, savedCancelled.getStatus());
    }

    @Test
    void shouldHandleCancelledStatus() {
        // Given
        Sale sale = adapter.save(new Sale(
            new SaleNumber("SALE-CANCEL-TEST"),
            1L,
            1L,
            LocalDate.of(2024, 1, 1),
            "CASH",
            "To be cancelled"
        ));

        // When - cancel the sale
        Sale cancelled = new Sale(
            sale.getId(),
            sale.getSaleNumber(),
            sale.getCustomerId(),
            sale.getEmployeeId(),
            sale.getSaleDate(),
            SaleStatus.CANCELLED,
            sale.getPaymentMethod(),
            sale.getTotalAmount(),
            sale.getNotes(),
            sale.getCreatedAt(),
            sale.getUpdatedAt()
        );
        Sale savedCancelled = adapter.save(cancelled);

        // Then
        assertEquals(SaleStatus.CANCELLED, savedCancelled.getStatus());
        
        // Verify we can find cancelled sales
        List<Sale> cancelledSales = adapter.findByStatus(SaleStatus.CANCELLED);
        assertEquals(1, cancelledSales.size());
        assertEquals(savedCancelled.getId(), cancelledSales.get(0).getId());
    }

    @Test
    void shouldHandleLargeTotalAmounts() {
        // Given
        Sale sale = new Sale(
            new SaleNumber("SALE-LARGE"),
            1L,
            1L,
            LocalDate.of(2024, 1, 1),
            "CREDIT_CARD",
            "Large amount sale"
        );
        Sale saved = adapter.save(sale);
        
        // Update with large amount
        Sale updated = new Sale(
            saved.getId(),
            saved.getSaleNumber(),
            saved.getCustomerId(),
            saved.getEmployeeId(),
            saved.getSaleDate(),
            saved.getStatus(),
            saved.getPaymentMethod(),
            new BigDecimal("999999.99"),
            saved.getNotes(),
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );

        // When
        Sale result = adapter.save(updated);
        Optional<Sale> retrieved = adapter.findById(result.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(0, new BigDecimal("999999.99").compareTo(retrieved.get().getTotalAmount()));
    }

    @Test
    void shouldHandleFutureDates() {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(30);
        
        Sale sale = new Sale(
            new SaleNumber("SALE-FUTURE"),
            1L,
            1L,
            futureDate,
            "CASH",
            "Future dated sale"
        );

        // When
        Sale saved = adapter.save(sale);
        Optional<Sale> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(futureDate, retrieved.get().getSaleDate());
    }

    @Test
    void shouldHandleDifferentPaymentMethods() {
        // Given
        String[] paymentMethods = {"CASH", "CREDIT_CARD", "DEBIT_CARD", "BANK_TRANSFER", "CHECK"};
        
        for (int i = 0; i < paymentMethods.length; i++) {
            Sale sale = new Sale(
                new SaleNumber("SALE-PM-" + i),
                1L,
                1L,
                LocalDate.of(2024, 1, 1),
                paymentMethods[i],
                "Payment method test"
            );
            
            // When
            Sale saved = adapter.save(sale);
            
            // Then
            assertEquals(paymentMethods[i], saved.getPaymentMethod());
        }
    }

    @Test
    void shouldHandleMultipleSalesForSameCustomer() {
        // Given
        Long customerId = 1L;
        
        for (int i = 1; i <= 5; i++) {
            adapter.save(new Sale(
                new SaleNumber("SALE-MULTI-" + i),
                customerId,
                1L,
                LocalDate.of(2024, 1, i),
                "CASH",
                "Sale " + i
            ));
        }

        // When
        List<Sale> customerSales = adapter.findByCustomerId(customerId);

        // Then
        assertEquals(5, customerSales.size());
        assertTrue(customerSales.stream().allMatch(s -> s.getCustomerId().equals(customerId)));
    }
}
