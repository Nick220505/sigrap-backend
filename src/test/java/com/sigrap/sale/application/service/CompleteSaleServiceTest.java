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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteSaleServiceTest {
    
    @Mock
    private SaleRepositoryPort saleRepository;
    
    @InjectMocks
    private CompleteSaleService completeSaleService;
    
    @BeforeEach
    void setUp() {
        reset(saleRepository);
    }
    
    @Test
    void shouldCompleteSaleSuccessfully() {
        // Given
        SaleId id = new SaleId(1L);
        Sale testSale = new Sale(
            id,
            new SaleNumber("SALE-001"),
            1L,
            2L,
            LocalDateTime.now(),
            BigDecimal.valueOf(100),
            PaymentMethod.CASH,
            SaleStatus.PENDING,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        // Add an item so sale can be completed
        testSale.addItem(new SaleItem(
            new SaleId(1L),
            1L,
            2,
            BigDecimal.valueOf(50)
        ));
        
        when(saleRepository.findById(id)).thenReturn(Optional.of(testSale));
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Sale result = completeSaleService.complete(id);
        
        // Then
        assertEquals(SaleStatus.COMPLETED, result.getStatus());
        verify(saleRepository).findById(id);
        verify(saleRepository).save(any(Sale.class));
    }
    
    @Test
    void shouldThrowExceptionWhenSaleNotFound() {
        // Given
        SaleId id = new SaleId(999L);
        when(saleRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> completeSaleService.complete(id));
        verify(saleRepository, never()).save(any(Sale.class));
    }
    
    @Test
    void shouldThrowExceptionWhenSaleHasNoItems() {
        // Given
        SaleId id = new SaleId(1L);
        Sale testSale = new Sale(
            id,
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
        
        when(saleRepository.findById(id)).thenReturn(Optional.of(testSale));
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> completeSaleService.complete(id));
        verify(saleRepository, never()).save(any(Sale.class));
    }
}
