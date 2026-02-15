package com.sigrap.sale.application.service;

import com.sigrap.exception.ResourceNotFoundException;
import com.sigrap.sale.application.port.in.command.UpdateSaleCommand;
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
class UpdateSaleServiceTest {
    
    @Mock
    private SaleRepositoryPort saleRepository;
    
    @InjectMocks
    private UpdateSaleService updateSaleService;
    
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
            "Original notes",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    @Test
    void shouldUpdatePaymentMethod() {
        // Given
        SaleId id = new SaleId(1L);
        UpdateSaleCommand command = new UpdateSaleCommand(PaymentMethod.CREDIT_CARD, null);
        
        when(saleRepository.findById(id)).thenReturn(Optional.of(testSale));
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Sale result = updateSaleService.update(id, command);
        
        // Then
        assertEquals(PaymentMethod.CREDIT_CARD, result.getPaymentMethod());
        verify(saleRepository).findById(id);
        verify(saleRepository).save(any(Sale.class));
    }
    
    @Test
    void shouldUpdateNotes() {
        // Given
        SaleId id = new SaleId(1L);
        UpdateSaleCommand command = new UpdateSaleCommand(null, "Updated notes");
        
        when(saleRepository.findById(id)).thenReturn(Optional.of(testSale));
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Sale result = updateSaleService.update(id, command);
        
        // Then
        assertEquals("Updated notes", result.getNotes());
        verify(saleRepository).findById(id);
        verify(saleRepository).save(any(Sale.class));
    }
    
    @Test
    void shouldUpdateBothFields() {
        // Given
        SaleId id = new SaleId(1L);
        UpdateSaleCommand command = new UpdateSaleCommand(PaymentMethod.DEBIT_CARD, "New notes");
        
        when(saleRepository.findById(id)).thenReturn(Optional.of(testSale));
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Sale result = updateSaleService.update(id, command);
        
        // Then
        assertEquals(PaymentMethod.DEBIT_CARD, result.getPaymentMethod());
        assertEquals("New notes", result.getNotes());
        verify(saleRepository).save(any(Sale.class));
    }
    
    @Test
    void shouldThrowExceptionWhenSaleNotFound() {
        // Given
        SaleId id = new SaleId(999L);
        UpdateSaleCommand command = new UpdateSaleCommand(PaymentMethod.CASH, "Notes");
        
        when(saleRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> updateSaleService.update(id, command));
        verify(saleRepository).findById(id);
        verify(saleRepository, never()).save(any(Sale.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUpdatingCompletedSale() {
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
            "Notes",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        SaleId id = new SaleId(1L);
        UpdateSaleCommand command = new UpdateSaleCommand(PaymentMethod.CREDIT_CARD, null);
        
        when(saleRepository.findById(id)).thenReturn(Optional.of(completedSale));
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> updateSaleService.update(id, command));
        verify(saleRepository, never()).save(any(Sale.class));
    }
}
