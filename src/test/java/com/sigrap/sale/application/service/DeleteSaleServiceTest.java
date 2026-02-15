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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteSaleServiceTest {
    
    @Mock
    private SaleRepositoryPort saleRepository;
    
    @InjectMocks
    private DeleteSaleService deleteSaleService;
    
    @BeforeEach
    void setUp() {
        reset(saleRepository);
    }
    
    @Test
    void shouldDeleteSaleSuccessfully() {
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
        doNothing().when(saleRepository).deleteById(id);
        
        // When
        deleteSaleService.delete(id);
        
        // Then
        verify(saleRepository).findById(id);
        verify(saleRepository).deleteById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenSaleNotFound() {
        // Given
        SaleId id = new SaleId(999L);
        when(saleRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> deleteSaleService.delete(id));
        verify(saleRepository).findById(id);
        verify(saleRepository, never()).deleteById(any());
    }
}
