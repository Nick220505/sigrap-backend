package com.sigrap.sale.infrastructure.adapter.out.persistence;

import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleReturn;
import com.sigrap.sale.domain.model.SaleReturnId;
import com.sigrap.sale.domain.model.SaleReturnNumber;
import com.sigrap.sale.domain.model.SaleReturnStatus;
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
 * Integration tests for SaleReturnPersistenceAdapter.
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
class SaleReturnPersistenceAdapterTest {

    @Autowired
    private SaleReturnPersistenceAdapter adapter;

    @Autowired
    private SaleReturnJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewSaleReturn() {
        // Given
        SaleReturn saleReturn = new SaleReturn(
            new SaleReturnNumber("RET-2024-001"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 15),
            "Defective product",
            new BigDecimal("50.00"),
            "Customer requested refund"
        );

        // When
        SaleReturn saved = adapter.save(saleReturn);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("RET-2024-001", saved.getReturnNumber().value());
        assertEquals(1L, saved.getSaleId().value());
        assertEquals(LocalDate.of(2024, 1, 15), saved.getReturnDate());
        assertEquals("Defective product", saved.getReason());
        assertEquals(0, new BigDecimal("50.00").compareTo(saved.getRefundAmount()));
        assertEquals("Customer requested refund", saved.getNotes());
        assertEquals(SaleReturnStatus.PENDING, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldUpdateExistingSaleReturn() {
        // Given - save initial sale return
        SaleReturn saleReturn = new SaleReturn(
            new SaleReturnNumber("RET-2024-002"),
            new SaleId(2L),
            LocalDate.of(2024, 1, 20),
            "Wrong item",
            new BigDecimal("75.00"),
            "Original notes"
        );
        SaleReturn saved = adapter.save(saleReturn);
        
        // When - update the sale return
        SaleReturn updated = new SaleReturn(
            saved.getId(),
            saved.getReturnNumber(),
            saved.getSaleId(),
            saved.getReturnDate(),
            saved.getReason(),
            SaleReturnStatus.APPROVED,
            saved.getRefundAmount(),
            "Updated notes",
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );
        SaleReturn result = adapter.save(updated);

        // Then
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Updated notes", result.getNotes());
        assertEquals(SaleReturnStatus.APPROVED, result.getStatus());
    }

    @Test
    void shouldFindSaleReturnById() {
        // Given
        SaleReturn saleReturn = new SaleReturn(
            new SaleReturnNumber("RET-2024-003"),
            new SaleId(3L),
            LocalDate.of(2024, 1, 25),
            "Damaged",
            new BigDecimal("100.00"),
            "Test return"
        );
        SaleReturn saved = adapter.save(saleReturn);

        // When
        Optional<SaleReturn> found = adapter.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("RET-2024-003", found.get().getReturnNumber().value());
        assertEquals(3L, found.get().getSaleId().value());
    }

    @Test
    void shouldReturnEmptyWhenSaleReturnNotFound() {
        // Given
        SaleReturnId nonExistentId = new SaleReturnId(999L);

        // When
        Optional<SaleReturn> found = adapter.findById(nonExistentId);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllSaleReturns() {
        // Given
        adapter.save(new SaleReturn(
            new SaleReturnNumber("RET-001"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 1),
            "Reason 1",
            new BigDecimal("50.00"),
            "Return 1"
        ));
        adapter.save(new SaleReturn(
            new SaleReturnNumber("RET-002"),
            new SaleId(2L),
            LocalDate.of(2024, 1, 2),
            "Reason 2",
            new BigDecimal("75.00"),
            "Return 2"
        ));
        adapter.save(new SaleReturn(
            new SaleReturnNumber("RET-003"),
            new SaleId(3L),
            LocalDate.of(2024, 1, 3),
            "Reason 3",
            new BigDecimal("100.00"),
            null
        ));

        // When
        List<SaleReturn> returns = adapter.findAll();

        // Then
        assertNotNull(returns);
        assertEquals(3, returns.size());
        assertTrue(returns.stream().anyMatch(r -> r.getReturnNumber().value().equals("RET-001")));
        assertTrue(returns.stream().anyMatch(r -> r.getReturnNumber().value().equals("RET-002")));
        assertTrue(returns.stream().anyMatch(r -> r.getReturnNumber().value().equals("RET-003")));
    }

    @Test
    void shouldReturnEmptyListWhenNoSaleReturns() {
        // When
        List<SaleReturn> returns = adapter.findAll();

        // Then
        assertNotNull(returns);
        assertTrue(returns.isEmpty());
    }

    @Test
    void shouldFindSaleReturnsBySaleId() {
        // Given
        SaleId saleId1 = new SaleId(1L);
        SaleId saleId2 = new SaleId(2L);
        
        adapter.save(new SaleReturn(
            new SaleReturnNumber("RET-S1-001"),
            saleId1,
            LocalDate.of(2024, 1, 1),
            "Reason 1",
            new BigDecimal("50.00"),
            "Sale 1 return 1"
        ));
        adapter.save(new SaleReturn(
            new SaleReturnNumber("RET-S1-002"),
            saleId1,
            LocalDate.of(2024, 1, 2),
            "Reason 2",
            new BigDecimal("75.00"),
            "Sale 1 return 2"
        ));
        adapter.save(new SaleReturn(
            new SaleReturnNumber("RET-S2-001"),
            saleId2,
            LocalDate.of(2024, 1, 3),
            "Reason 3",
            new BigDecimal("100.00"),
            "Sale 2 return 1"
        ));

        // When
        List<SaleReturn> sale1Returns = adapter.findBySaleId(saleId1);
        List<SaleReturn> sale2Returns = adapter.findBySaleId(saleId2);

        // Then
        assertEquals(2, sale1Returns.size());
        assertTrue(sale1Returns.stream().allMatch(r -> r.getSaleId().equals(saleId1)));
        
        assertEquals(1, sale2Returns.size());
        assertTrue(sale2Returns.stream().allMatch(r -> r.getSaleId().equals(saleId2)));
    }

    @Test
    void shouldReturnEmptyListWhenNoReturnsForSale() {
        // Given
        SaleId nonExistentSaleId = new SaleId(999L);

        // When
        List<SaleReturn> returns = adapter.findBySaleId(nonExistentSaleId);

        // Then
        assertNotNull(returns);
        assertTrue(returns.isEmpty());
    }

    @Test
    void shouldFindSaleReturnsByStatus() {
        // Given
        SaleReturn pending1 = adapter.save(new SaleReturn(
            new SaleReturnNumber("RET-PENDING-001"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 1),
            "Reason 1",
            new BigDecimal("50.00"),
            "Pending return 1"
        ));
        
        SaleReturn pending2 = adapter.save(new SaleReturn(
            new SaleReturnNumber("RET-PENDING-002"),
            new SaleId(2L),
            LocalDate.of(2024, 1, 2),
            "Reason 2",
            new BigDecimal("75.00"),
            "Pending return 2"
        ));
        
        // Approve one return
        SaleReturn approved = new SaleReturn(
            pending1.getId(),
            pending1.getReturnNumber(),
            pending1.getSaleId(),
            pending1.getReturnDate(),
            pending1.getReason(),
            SaleReturnStatus.APPROVED,
            pending1.getRefundAmount(),
            pending1.getNotes(),
            pending1.getCreatedAt(),
            pending1.getUpdatedAt()
        );
        adapter.save(approved);

        // When
        List<SaleReturn> pendingReturns = adapter.findByStatus(SaleReturnStatus.PENDING);
        List<SaleReturn> approvedReturns = adapter.findByStatus(SaleReturnStatus.APPROVED);

        // Then
        assertEquals(1, pendingReturns.size());
        assertTrue(pendingReturns.stream().allMatch(r -> r.getStatus() == SaleReturnStatus.PENDING));
        
        assertEquals(1, approvedReturns.size());
        assertTrue(approvedReturns.stream().allMatch(r -> r.getStatus() == SaleReturnStatus.APPROVED));
    }

    @Test
    void shouldReturnEmptyListWhenNoReturnsWithStatus() {
        // Given - create only pending returns
        adapter.save(new SaleReturn(
            new SaleReturnNumber("RET-001"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 1),
            "Reason",
            new BigDecimal("50.00"),
            "Pending return"
        ));

        // When
        List<SaleReturn> completedReturns = adapter.findByStatus(SaleReturnStatus.COMPLETED);

        // Then
        assertNotNull(completedReturns);
        assertTrue(completedReturns.isEmpty());
    }

    @Test
    void shouldDeleteSaleReturnById() {
        // Given
        SaleReturn saleReturn = new SaleReturn(
            new SaleReturnNumber("RET-TEMP"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 1),
            "Reason",
            new BigDecimal("50.00"),
            "Temporary return"
        );
        SaleReturn saved = adapter.save(saleReturn);
        SaleReturnId savedId = saved.getId();

        // When
        adapter.deleteById(savedId);

        // Then
        Optional<SaleReturn> found = adapter.findById(savedId);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldPreserveTimestampsWhenSaving() {
        // Given
        SaleReturn saleReturn = new SaleReturn(
            new SaleReturnNumber("RET-TEST"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 1),
            "Reason",
            new BigDecimal("50.00"),
            "Test return"
        );

        // When
        SaleReturn saved = adapter.save(saleReturn);

        // Then
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldHandleSaleReturnWithNullNotes() {
        // Given
        SaleReturn saleReturn = new SaleReturn(
            new SaleReturnNumber("RET-NO-NOTES"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 1),
            "Reason",
            new BigDecimal("50.00"),
            null
        );

        // When
        SaleReturn saved = adapter.save(saleReturn);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("RET-NO-NOTES", saved.getReturnNumber().value());
        assertNull(saved.getNotes());
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        // Given
        SaleReturn saleReturn = new SaleReturn(
            new SaleReturnNumber("RET-COMPLETE"),
            new SaleId(5L),
            LocalDate.of(2024, 3, 15),
            "Complete reason",
            new BigDecimal("150.50"),
            "Complete return with all fields"
        );

        // When
        SaleReturn saved = adapter.save(saleReturn);
        Optional<SaleReturn> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        SaleReturn result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(saved.getReturnNumber().value(), result.getReturnNumber().value());
        assertEquals(saved.getSaleId().value(), result.getSaleId().value());
        assertEquals(saved.getReturnDate(), result.getReturnDate());
        assertEquals(saved.getReason(), result.getReason());
        assertEquals(saved.getRefundAmount(), result.getRefundAmount());
        assertEquals(saved.getNotes(), result.getNotes());
        assertEquals(saved.getStatus(), result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void shouldHandleAllStatusTransitions() {
        // Given
        SaleReturn saleReturn = adapter.save(new SaleReturn(
            new SaleReturnNumber("RET-STATUS-TEST"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 1),
            "Reason",
            new BigDecimal("50.00"),
            "Status transition test"
        ));

        // Test PENDING -> APPROVED
        SaleReturn approved = new SaleReturn(
            saleReturn.getId(),
            saleReturn.getReturnNumber(),
            saleReturn.getSaleId(),
            saleReturn.getReturnDate(),
            saleReturn.getReason(),
            SaleReturnStatus.APPROVED,
            saleReturn.getRefundAmount(),
            saleReturn.getNotes(),
            saleReturn.getCreatedAt(),
            saleReturn.getUpdatedAt()
        );
        SaleReturn savedApproved = adapter.save(approved);
        assertEquals(SaleReturnStatus.APPROVED, savedApproved.getStatus());

        // Test APPROVED -> COMPLETED
        SaleReturn completed = new SaleReturn(
            savedApproved.getId(),
            savedApproved.getReturnNumber(),
            savedApproved.getSaleId(),
            savedApproved.getReturnDate(),
            savedApproved.getReason(),
            SaleReturnStatus.COMPLETED,
            savedApproved.getRefundAmount(),
            savedApproved.getNotes(),
            savedApproved.getCreatedAt(),
            savedApproved.getUpdatedAt()
        );
        SaleReturn savedCompleted = adapter.save(completed);
        assertEquals(SaleReturnStatus.COMPLETED, savedCompleted.getStatus());
    }

    @Test
    void shouldHandleRejectedStatus() {
        // Given
        SaleReturn saleReturn = adapter.save(new SaleReturn(
            new SaleReturnNumber("RET-REJECT-TEST"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 1),
            "Reason",
            new BigDecimal("50.00"),
            "To be rejected"
        ));

        // When - reject the return
        SaleReturn rejected = new SaleReturn(
            saleReturn.getId(),
            saleReturn.getReturnNumber(),
            saleReturn.getSaleId(),
            saleReturn.getReturnDate(),
            saleReturn.getReason(),
            SaleReturnStatus.REJECTED,
            saleReturn.getRefundAmount(),
            saleReturn.getNotes(),
            saleReturn.getCreatedAt(),
            saleReturn.getUpdatedAt()
        );
        SaleReturn savedRejected = adapter.save(rejected);

        // Then
        assertEquals(SaleReturnStatus.REJECTED, savedRejected.getStatus());
        
        // Verify we can find rejected returns
        List<SaleReturn> rejectedReturns = adapter.findByStatus(SaleReturnStatus.REJECTED);
        assertEquals(1, rejectedReturns.size());
        assertEquals(savedRejected.getId(), rejectedReturns.get(0).getId());
    }

    @Test
    void shouldHandleLargeRefundAmounts() {
        // Given
        SaleReturn saleReturn = new SaleReturn(
            new SaleReturnNumber("RET-LARGE"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 1),
            "Reason",
            new BigDecimal("999999.99"),
            "Large refund amount"
        );

        // When
        SaleReturn saved = adapter.save(saleReturn);
        Optional<SaleReturn> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(0, new BigDecimal("999999.99").compareTo(retrieved.get().getRefundAmount()));
    }

    @Test
    void shouldHandleFutureDates() {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(30);
        
        SaleReturn saleReturn = new SaleReturn(
            new SaleReturnNumber("RET-FUTURE"),
            new SaleId(1L),
            futureDate,
            "Reason",
            new BigDecimal("50.00"),
            "Future dated return"
        );

        // When
        SaleReturn saved = adapter.save(saleReturn);
        Optional<SaleReturn> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(futureDate, retrieved.get().getReturnDate());
    }

    @Test
    void shouldHandleDifferentReturnReasons() {
        // Given
        String[] reasons = {
            "Defective product",
            "Wrong item received",
            "Changed mind",
            "Product damaged",
            "Not as described"
        };
        
        for (int i = 0; i < reasons.length; i++) {
            SaleReturn saleReturn = new SaleReturn(
                new SaleReturnNumber("RET-REASON-" + i),
                new SaleId(1L),
                LocalDate.of(2024, 1, 1),
                reasons[i],
                new BigDecimal("50.00"),
                "Return reason test"
            );
            
            // When
            SaleReturn saved = adapter.save(saleReturn);
            
            // Then
            assertEquals(reasons[i], saved.getReason());
        }
    }

    @Test
    void shouldHandleMultipleReturnsForSameSale() {
        // Given
        SaleId saleId = new SaleId(1L);
        
        for (int i = 1; i <= 3; i++) {
            adapter.save(new SaleReturn(
                new SaleReturnNumber("RET-MULTI-" + i),
                saleId,
                LocalDate.of(2024, 1, i),
                "Reason " + i,
                new BigDecimal("50.00"),
                "Return " + i
            ));
        }

        // When
        List<SaleReturn> saleReturns = adapter.findBySaleId(saleId);

        // Then
        assertEquals(3, saleReturns.size());
        assertTrue(saleReturns.stream().allMatch(r -> r.getSaleId().equals(saleId)));
    }

    @Test
    void shouldHandleZeroRefundAmount() {
        // Given
        SaleReturn saleReturn = new SaleReturn(
            new SaleReturnNumber("RET-ZERO"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 1),
            "Exchange only",
            BigDecimal.ZERO,
            "No refund, exchange only"
        );

        // When
        SaleReturn saved = adapter.save(saleReturn);

        // Then
        assertEquals(0, BigDecimal.ZERO.compareTo(saved.getRefundAmount()));
    }

    @Test
    void shouldHandleDecimalRefundAmounts() {
        // Given
        SaleReturn saleReturn = new SaleReturn(
            new SaleReturnNumber("RET-DECIMAL"),
            new SaleId(1L),
            LocalDate.of(2024, 1, 1),
            "Reason",
            new BigDecimal("123.45"),
            "Decimal refund amount"
        );

        // When
        SaleReturn saved = adapter.save(saleReturn);
        Optional<SaleReturn> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(0, new BigDecimal("123.45").compareTo(retrieved.get().getRefundAmount()));
    }
}
