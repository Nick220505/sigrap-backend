package com.sigrap.sale.infrastructure.adapter.out.persistence;

import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleItem;
import com.sigrap.sale.domain.model.SaleItemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SaleItemPersistenceAdapter.
 * Tests the adapter with a real database (H2) using Spring Boot test context.
 * 
 * These tests verify:
 * - Correct mapping between domain and JPA entities
 * - Database operations work as expected
 * - The adapter properly implements the repository port
 * - Quantity and price calculations are preserved
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SaleItemPersistenceAdapterTest {

    @Autowired
    private SaleItemPersistenceAdapter adapter;

    @Autowired
    private SaleItemJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewSaleItem() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            2,
            new BigDecimal("50.00")
        );

        // When
        SaleItem saved = adapter.save(saleItem);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getSaleId().value());
        assertEquals(1L, saved.getProductId());
        assertEquals(2, saved.getQuantity());
        assertEquals(0, new BigDecimal("50.00").compareTo(saved.getUnitPrice()));
        assertEquals(0, new BigDecimal("100.00").compareTo(saved.getSubtotal()));
    }

    @Test
    void shouldUpdateExistingSaleItem() {
        // Given - save initial sale item
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            2,
            new BigDecimal("50.00")
        );
        SaleItem saved = adapter.save(saleItem);
        
        // When - update the sale item
        SaleItem updated = new SaleItem(
            saved.getId(),
            saved.getSaleId(),
            saved.getProductId(),
            3,
            new BigDecimal("45.00")
        );
        SaleItem result = adapter.save(updated);

        // Then
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals(3, result.getQuantity());
        assertEquals(0, new BigDecimal("45.00").compareTo(result.getUnitPrice()));
        assertEquals(0, new BigDecimal("135.00").compareTo(result.getSubtotal()));
    }

    @Test
    void shouldFindSaleItemById() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            2,
            new BigDecimal("50.00"),
            new BigDecimal("5.00")
        );
        SaleItem saved = adapter.save(saleItem);

        // When
        Optional<SaleItem> found = adapter.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(1L, found.get().getSaleId().value());
        assertEquals(1L, found.get().getProductId());
    }

    @Test
    void shouldReturnEmptyWhenSaleItemNotFound() {
        // Given
        SaleItemId nonExistentId = new SaleItemId(999L);

        // When
        Optional<SaleItem> found = adapter.findById(nonExistentId);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindSaleItemsBySaleId() {
        // Given
        SaleId saleId1 = new SaleId(1L);
        SaleId saleId2 = new SaleId(2L);
        
        adapter.save(new SaleItem(
            saleId1,
            1L,
            2,
            new BigDecimal("50.00"),
            new BigDecimal("5.00")
        ));
        adapter.save(new SaleItem(
            saleId1,
            2L,
            1,
            new BigDecimal("30.00"),
            BigDecimal.ZERO
        ));
        adapter.save(new SaleItem(
            saleId2,
            3L,
            3,
            new BigDecimal("20.00"),
            new BigDecimal("2.00")
        ));

        // When
        List<SaleItem> sale1Items = adapter.findBySaleId(saleId1);
        List<SaleItem> sale2Items = adapter.findBySaleId(saleId2);

        // Then
        assertEquals(2, sale1Items.size());
        assertTrue(sale1Items.stream().allMatch(item -> item.getSaleId().equals(saleId1)));
        
        assertEquals(1, sale2Items.size());
        assertTrue(sale2Items.stream().allMatch(item -> item.getSaleId().equals(saleId2)));
    }

    @Test
    void shouldReturnEmptyListWhenNoItemsForSale() {
        // Given
        SaleId nonExistentSaleId = new SaleId(999L);

        // When
        List<SaleItem> items = adapter.findBySaleId(nonExistentSaleId);

        // Then
        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    void shouldDeleteSaleItemById() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            2,
            new BigDecimal("50.00"),
            new BigDecimal("5.00")
        );
        SaleItem saved = adapter.save(saleItem);
        SaleItemId savedId = saved.getId();

        // When
        adapter.deleteById(savedId);

        // Then
        Optional<SaleItem> found = adapter.findById(savedId);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldPreserveTimestampsWhenSaving() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            2,
            new BigDecimal("50.00"),
            new BigDecimal("5.00")
        );

        // When
        SaleItem saved = adapter.save(saleItem);

        // Then
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldHandleSaleItemWithZeroDiscount() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            2,
            new BigDecimal("50.00"),
            BigDecimal.ZERO
        );

        // When
        SaleItem saved = adapter.save(saleItem);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(0, BigDecimal.ZERO.compareTo(saved.getDiscount()));
        assertEquals(0, new BigDecimal("100.00").compareTo(saved.getSubtotal()));
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(5L),
            5L,
            10,
            new BigDecimal("25.50"),
            new BigDecimal("12.75")
        );

        // When
        SaleItem saved = adapter.save(saleItem);
        Optional<SaleItem> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        SaleItem result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(saved.getSaleId().value(), result.getSaleId().value());
        assertEquals(saved.getProductId(), result.getProductId());
        assertEquals(saved.getQuantity(), result.getQuantity());
        assertEquals(saved.getUnitPrice(), result.getUnitPrice());
        assertEquals(saved.getDiscount(), result.getDiscount());
        assertEquals(saved.getSubtotal(), result.getSubtotal());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void shouldCalculateSubtotalCorrectly() {
        // Given - quantity: 5, unit price: 20.00, discount: 10.00
        // Expected subtotal: (5 * 20.00) - 10.00 = 90.00
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            5,
            new BigDecimal("20.00"),
            new BigDecimal("10.00")
        );

        // When
        SaleItem saved = adapter.save(saleItem);
        Optional<SaleItem> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(0, new BigDecimal("90.00").compareTo(retrieved.get().getSubtotal()));
    }

    @Test
    void shouldHandleLargeQuantities() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            1000,
            new BigDecimal("10.00"),
            new BigDecimal("100.00")
        );

        // When
        SaleItem saved = adapter.save(saleItem);
        Optional<SaleItem> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(1000, retrieved.get().getQuantity());
        assertEquals(0, new BigDecimal("9900.00").compareTo(retrieved.get().getSubtotal()));
    }

    @Test
    void shouldHandleDecimalPrices() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            3,
            new BigDecimal("15.99"),
            new BigDecimal("2.50")
        );

        // When
        SaleItem saved = adapter.save(saleItem);
        Optional<SaleItem> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(0, new BigDecimal("15.99").compareTo(retrieved.get().getUnitPrice()));
        assertEquals(0, new BigDecimal("2.50").compareTo(retrieved.get().getDiscount()));
        // Subtotal: (3 * 15.99) - 2.50 = 45.47
        assertEquals(0, new BigDecimal("45.47").compareTo(retrieved.get().getSubtotal()));
    }

    @Test
    void shouldHandleMultipleItemsForSameSale() {
        // Given
        SaleId saleId = new SaleId(1L);
        
        for (int i = 1; i <= 5; i++) {
            adapter.save(new SaleItem(
                saleId,
                (long) i,
                i,
                new BigDecimal("10.00"),
                BigDecimal.ZERO
            ));
        }

        // When
        List<SaleItem> saleItems = adapter.findBySaleId(saleId);

        // Then
        assertEquals(5, saleItems.size());
        assertTrue(saleItems.stream().allMatch(item -> item.getSaleId().equals(saleId)));
    }

    @Test
    void shouldHandleSingleQuantityItem() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            1,
            new BigDecimal("100.00"),
            new BigDecimal("10.00")
        );

        // When
        SaleItem saved = adapter.save(saleItem);

        // Then
        assertEquals(1, saved.getQuantity());
        assertEquals(0, new BigDecimal("90.00").compareTo(saved.getSubtotal()));
    }

    @Test
    void shouldHandleFullDiscountItem() {
        // Given - discount equals total price
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            2,
            new BigDecimal("50.00"),
            new BigDecimal("100.00")
        );

        // When
        SaleItem saved = adapter.save(saleItem);

        // Then
        assertEquals(0, new BigDecimal("0.00").compareTo(saved.getSubtotal()));
    }

    @Test
    void shouldPreserveHighPrecisionDecimals() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            3,
            new BigDecimal("33.333"),
            new BigDecimal("1.111")
        );

        // When
        SaleItem saved = adapter.save(saleItem);
        Optional<SaleItem> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        // Subtotal: (3 * 33.333) - 1.111 = 98.888
        assertEquals(0, new BigDecimal("98.888").compareTo(retrieved.get().getSubtotal()));
    }
}
