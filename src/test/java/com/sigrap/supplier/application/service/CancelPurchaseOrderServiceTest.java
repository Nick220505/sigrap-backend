package com.sigrap.supplier.application.service;

import com.sigrap.supplier.domain.model.*;
import com.sigrap.supplier.domain.port.PurchaseOrderRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelPurchaseOrderServiceTest {
    
    @Mock
    private PurchaseOrderRepositoryPort purchaseOrderRepository;
    
    @InjectMocks
    private CancelPurchaseOrderService cancelPurchaseOrderService;
    
    @Test
    void shouldCancelPendingPurchaseOrder() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(1L);
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            id,
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            PurchaseOrderStatus.PENDING,
            new BigDecimal("1000.00"),
            "Notes",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(purchaseOrder));
        when(purchaseOrderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        PurchaseOrder result = cancelPurchaseOrderService.cancel(id);
        
        // Then
        assertNotNull(result);
        assertEquals(PurchaseOrderStatus.CANCELLED, result.getStatus());
        
        verify(purchaseOrderRepository).findById(id);
        verify(purchaseOrderRepository).save(purchaseOrder);
    }
    
    @Test
    void shouldCancelApprovedPurchaseOrder() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(1L);
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            id,
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            PurchaseOrderStatus.APPROVED,
            new BigDecimal("1000.00"),
            "Notes",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(purchaseOrder));
        when(purchaseOrderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        PurchaseOrder result = cancelPurchaseOrderService.cancel(id);
        
        // Then
        assertNotNull(result);
        assertEquals(PurchaseOrderStatus.CANCELLED, result.getStatus());
        verify(purchaseOrderRepository).save(purchaseOrder);
    }
    
    @Test
    void shouldThrowExceptionWhenPurchaseOrderNotFound() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(999L);
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> cancelPurchaseOrderService.cancel(id)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(purchaseOrderRepository).findById(id);
        verify(purchaseOrderRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenPurchaseOrderIsReceived() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(1L);
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            id,
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            PurchaseOrderStatus.RECEIVED,
            new BigDecimal("1000.00"),
            "Notes",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(purchaseOrder));
        
        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> cancelPurchaseOrderService.cancel(id)
        );
        
        assertTrue(exception.getMessage().contains("Cannot cancel a received"));
        verify(purchaseOrderRepository).findById(id);
        verify(purchaseOrderRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenPurchaseOrderIsAlreadyCancelled() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(1L);
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            id,
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            PurchaseOrderStatus.CANCELLED,
            new BigDecimal("1000.00"),
            "Notes",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(purchaseOrder));
        
        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> cancelPurchaseOrderService.cancel(id)
        );
        
        assertTrue(exception.getMessage().contains("already cancelled"));
        verify(purchaseOrderRepository).findById(id);
        verify(purchaseOrderRepository, never()).save(any());
    }
}
