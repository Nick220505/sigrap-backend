package com.sigrap.sale.infrastructure.adapter.out.persistence;

import com.sigrap.sale.domain.model.SaleId;
import com.sigrap.sale.domain.model.SaleItem;
import com.sigrap.sale.domain.model.SaleItemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
 * Tests the persistence layer for sale items using hexagonal architecture.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("SaleItem Persistence Adapter Tests")
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
        assertEquals(new SaleId(1L), saved.getSaleId());
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
            new BigDecimal("50.00")
        );
        SaleItem saved = adapter.save(saleItem);

        // When
        Optional<SaleItem> found = adapter.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenSaleItemNotFound() {
        // When
        Optional<SaleItem> found = adapter.findById(new SaleItemId(999L));

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindSaleItemsBySaleId() {
        // Given
        SaleId saleId1 = new SaleId(1L);
        SaleId saleId2 = new SaleId(2L);
        
        adapter.save(new SaleItem(saleId1, 1L, 2, new BigDecimal("50.00")));
        adapter.save(new SaleItem(saleId1, 2L, 3, new BigDecimal("30.00")));
        adapter.save(new SaleItem(saleId2, 3L, 1, new BigDecimal("100.00")));

        // When
        List<SaleItem> itemsForSale1 = adapter.findBySaleId(saleId1);
        List<SaleItem> itemsForSale2 = adapter.findBySaleId(saleId2);

        // Then
        assertEquals(2, itemsForSale1.size());
        assertEquals(1, itemsForSale2.size());
        assertTrue(itemsForSale1.stream().allMatch(item -> item.getSaleId().equals(saleId1)));
        assertTrue(itemsForSale2.stream().allMatch(item -> item.getSaleId().equals(saleId2)));
    }

    @Test
    void shouldReturnEmptyListWhenNoItemsForSale() {
        // When
        List<SaleItem> items = adapter.findBySaleId(new SaleId(999L));

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
            new BigDecimal("50.00")
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
    void shouldCalculateSubtotalCorrectly() {
        // Given - quantity: 5, unit price: 20.00
        // Expected subtotal: 5 * 20.00 = 100.00
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            5,
            new BigDecimal("20.00")
        );

        // When
        SaleItem saved = adapter.save(saleItem);

        // Then
        assertEquals(0, new BigDecimal("100.00").compareTo(saved.getSubtotal()));
    }

    @Test
    void shouldHandleLargeQuantities() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            1000,
            new BigDecimal("10.00")
        );

        // When
        SaleItem saved = adapter.save(saleItem);

        // Then
        assertNotNull(saved);
        assertEquals(1000, saved.getQuantity());
        assertEquals(0, new BigDecimal("10000.00").compareTo(saved.getSubtotal()));
    }

    @Test
    void shouldHandleDecimalPrices() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            3,
            new BigDecimal("12.99")
        );

        // When
        SaleItem saved = adapter.save(saleItem);

        // Then
        assertNotNull(saved);
        assertEquals(0, new BigDecimal("12.99").compareTo(saved.getUnitPrice()));
        assertEquals(0, new BigDecimal("38.97").compareTo(saved.getSubtotal()));
    }

    @Test
    void shouldHandleMultipleSaleItemsForSameSale() {
        // Given
        SaleId saleId = new SaleId(1L);
        
        for (int i = 1; i <= 5; i++) {
            adapter.save(new SaleItem(
                saleId,
                (long) i,
                i,
                new BigDecimal("10.00")
            ));
        }

        // When
        List<SaleItem> items = adapter.findBySaleId(saleId);

        // Then
        assertEquals(5, items.size());
    }

    @Test
    void shouldHandleSingleQuantityItem() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            1,
            new BigDecimal("99.99")
        );

        // When
        SaleItem saved = adapter.save(saleItem);

        // Then
        assertEquals(1, saved.getQuantity());
        assertEquals(0, new BigDecimal("99.99").compareTo(saved.getSubtotal()));
    }

    @Test
    void shouldPreserveHighPrecisionDecimals() {
        // Given
        SaleItem saleItem = new SaleItem(
            new SaleId(1L),
            1L,
            2,
            new BigDecimal("12.345")
        );

        // When
        SaleItem saved = adapter.save(saleItem);

        // Then
        assertEquals(0, new BigDecimal("12.345").compareTo(saved.getUnitPrice()));
        assertEquals(0, new BigDecimal("24.690").compareTo(saved.getSubtotal()));
    }
}
