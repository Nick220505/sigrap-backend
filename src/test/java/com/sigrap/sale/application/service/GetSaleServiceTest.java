package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.domain.model.*;
import com.sigrap.sale.domain.port.SaleRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSaleServiceTest {
    
    @Mock
    private SaleRepositoryPort saleRepository;
    
    @InjectMocks
    private GetSaleService getSaleService;
    
    private Sale testSale;
    
    @BeforeEach
    void setUp() {
        reset(saleRepository);
        testSale = new Sale(
            new SaleId(1L),
            new SaleNumber("SALE-001"),
            1L,
            2L,
            LocalDateTime.now(),
            BigDecimal.valueOf(100),
            PaymentMethod.CASH,
            SaleStatus.PENDING,
            "Test sale",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    @Test
    void shouldGetSaleById() {
        // Given
        SaleId id = new SaleId(1L);
        when(saleRepository.findById(id)).thenReturn(Optional.of(testSale));
        
        // When
        Sale result = getSaleService.getById(id);
        
        // Then
        assertNotNull(result);
        assertEquals(testSale.getId(), result.getId());
        verify(saleRepository).findById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenSaleNotFound() {
        // Given
        SaleId id = new SaleId(999L);
        when(saleRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> getSaleService.getById(id));
        verify(saleRepository).findById(id);
    }
    
    @Test
    void shouldFindSaleById() {
        // Given
        SaleId id = new SaleId(1L);
        when(saleRepository.findById(id)).thenReturn(Optional.of(testSale));
        
        // When
        Optional<Sale> result = getSaleService.findById(id);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testSale.getId(), result.get().getId());
        verify(saleRepository).findById(id);
    }
    
    @Test
    void shouldReturnEmptyWhenSaleNotFound() {
        // Given
        SaleId id = new SaleId(999L);
        when(saleRepository.findById(id)).thenReturn(Optional.empty());
        
        // When
        Optional<Sale> result = getSaleService.findById(id);
        
        // Then
        assertTrue(result.isEmpty());
        verify(saleRepository).findById(id);
    }
    
    @Test
    void shouldGetAllSales() {
        // Given
        Sale sale2 = new Sale(
            new SaleId(2L),
            new SaleNumber("SALE-002"),
            1L,
            2L,
            LocalDateTime.now(),
            BigDecimal.valueOf(200),
            PaymentMethod.CREDIT_CARD,
            SaleStatus.COMPLETED,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        when(saleRepository.findAll()).thenReturn(Arrays.asList(testSale, sale2));
        
        // When
        List<Sale> result = getSaleService.getAll();
        
        // Then
        assertEquals(2, result.size());
        verify(saleRepository).findAll();
    }
    
    @Test
    void shouldGetSalesByCustomerId() {
        // Given
        Long customerId = 1L;
        when(saleRepository.findByCustomerId(customerId)).thenReturn(List.of(testSale));
        
        // When
        List<Sale> result = getSaleService.getByCustomerId(customerId);
        
        // Then
        assertEquals(1, result.size());
        assertEquals(customerId, result.get(0).getCustomerId());
        verify(saleRepository).findByCustomerId(customerId);
    }
    
    @Test
    void shouldGetSalesByStatus() {
        // Given
        SaleStatus status = SaleStatus.PENDING;
        when(saleRepository.findByStatus(status)).thenReturn(List.of(testSale));
        
        // When
        List<Sale> result = getSaleService.getByStatus(status);
        
        // Then
        assertEquals(1, result.size());
        assertEquals(status, result.get(0).getStatus());
        verify(saleRepository).findByStatus(status);
    }
}
