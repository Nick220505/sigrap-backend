package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.domain.model.*;
import com.sigrap.sale.domain.port.SaleItemRepositoryPort;
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
class RemoveSaleItemServiceTest {
    
    @Mock
    private SaleItemRepositoryPort saleItemRepository;
    
    @Mock
    private SaleRepositoryPort saleRepository;
    
    @InjectMocks
    private RemoveSaleItemService removeSaleItemService;
    
    private Sale testSale;
    private SaleItem testItem;
    
    @BeforeEach
    void setUp() {
        reset(saleItemRepository, saleRepository);
        testSale = new Sale(
            new SaleId(1L),
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
        
        testItem = new SaleItem(
            new SaleItemId(1L),
            new SaleId(1L),
            10L,
            2,
            BigDecimal.valueOf(50)
        );
    }
    
    @Test
    void shouldRemoveItemFromSaleSuccessfully() {
        // Given
        SaleItemId id = new SaleItemId(1L);
        testSale.addItem(testItem);
        
        when(saleItemRepository.findById(id)).thenReturn(Optional.of(testItem));
        when(saleRepository.findById(any(SaleId.class))).thenReturn(Optional.of(testSale));
        doNothing().when(saleItemRepository).deleteById(id);
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        removeSaleItemService.removeItem(id);
        
        // Then
        verify(saleItemRepository).findById(id);
        verify(saleRepository).findById(any(SaleId.class));
        verify(saleItemRepository).deleteById(id);
        verify(saleRepository).save(any(Sale.class));
    }
    
    @Test
    void shouldThrowExceptionWhenItemNotFound() {
        // Given
        SaleItemId id = new SaleItemId(999L);
        when(saleItemRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> removeSaleItemService.removeItem(id));
        verify(saleItemRepository, never()).deleteById(any());
    }
    
    @Test
    void shouldThrowExceptionWhenRemovingFromCompletedSale() {
        // Given
        Sale completedSale = new Sale(
            new SaleId(1L),
            new SaleNumber("SALE-001"),
            1L,
            2L,
            LocalDateTime.now(),
            BigDecimal.valueOf(100),
            PaymentMethod.CASH,
            SaleStatus.COMPLETED,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        SaleItemId id = new SaleItemId(1L);
        when(saleItemRepository.findById(id)).thenReturn(Optional.of(testItem));
        when(saleRepository.findById(any(SaleId.class))).thenReturn(Optional.of(completedSale));
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> removeSaleItemService.removeItem(id));
        verify(saleItemRepository, never()).deleteById(any());
    }
}
