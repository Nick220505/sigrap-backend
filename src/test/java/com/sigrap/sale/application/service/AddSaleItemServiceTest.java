package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.command.AddSaleItemCommand;
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
class AddSaleItemServiceTest {
    
    @Mock
    private SaleItemRepositoryPort saleItemRepository;
    
    @Mock
    private SaleRepositoryPort saleRepository;
    
    @InjectMocks
    private AddSaleItemService addSaleItemService;
    
    private Sale testSale;
    
    @BeforeEach
    void setUp() {
        reset(saleItemRepository, saleRepository);
        testSale = new Sale(
            new SaleId(1L),
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
    }
    
    @Test
    void shouldAddItemToSaleSuccessfully() {
        // Given
        AddSaleItemCommand command = new AddSaleItemCommand(
            1L,
            10L,
            2,
            BigDecimal.valueOf(50)
        );
        
        when(saleRepository.findById(any(SaleId.class))).thenReturn(Optional.of(testSale));
        when(saleItemRepository.save(any(SaleItem.class))).thenAnswer(invocation -> {
            SaleItem item = invocation.getArgument(0);
            return new SaleItem(
                new SaleItemId(1L),
                item.getSaleId(),
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice()
            );
        });
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        SaleItem result = addSaleItemService.addItem(command);
        
        // Then
        assertNotNull(result);
        assertEquals(10L, result.getProductId());
        assertEquals(2, result.getQuantity());
        assertEquals(BigDecimal.valueOf(50), result.getUnitPrice());
        assertEquals(BigDecimal.valueOf(100), result.getSubtotal());
        
        verify(saleRepository).findById(any(SaleId.class));
        verify(saleItemRepository).save(any(SaleItem.class));
        verify(saleRepository).save(any(Sale.class));
    }
    
    @Test
    void shouldThrowExceptionWhenSaleNotFound() {
        // Given
        AddSaleItemCommand command = new AddSaleItemCommand(999L, 10L, 2, BigDecimal.valueOf(50));
        when(saleRepository.findById(any(SaleId.class))).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> addSaleItemService.addItem(command));
        verify(saleItemRepository, never()).save(any(SaleItem.class));
    }
    
    @Test
    void shouldThrowExceptionWhenAddingToCompletedSale() {
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
        
        AddSaleItemCommand command = new AddSaleItemCommand(1L, 10L, 2, BigDecimal.valueOf(50));
        when(saleRepository.findById(any(SaleId.class))).thenReturn(Optional.of(completedSale));
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> addSaleItemService.addItem(command));
        verify(saleItemRepository, never()).save(any(SaleItem.class));
    }
}
