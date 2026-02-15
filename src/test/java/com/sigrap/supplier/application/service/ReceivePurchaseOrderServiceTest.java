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
class ReceivePurchaseOrderServiceTest {
    
    @Mock
    private PurchaseOrderRepositoryPort purchaseOrderRepository;
    
    @InjectMocks
    private ReceivePurchaseOrderService receivePurchaseOrderService;
    
    @Test
    void shouldReceiveApprovedPurchaseOrder() {
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
        PurchaseOrder result = receivePurchaseOrderService.receive(id);
        
        // Then
        assertNotNull(result);
        assertEquals(PurchaseOrderStatus.RECEIVED, result.getStatus());
        
        verify(purchaseOrderRepository).findById(id);
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
            () -> receivePurchaseOrderService.receive(id)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(purchaseOrderRepository).findById(id);
        verify(purchaseOrderRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenPurchaseOrderIsNotApproved() {
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
        
        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> receivePurchaseOrderService.receive(id)
        );
        
        assertTrue(exception.getMessage().contains("Can only receive approved"));
        verify(purchaseOrderRepository).findById(id);
        verify(purchaseOrderRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenPurchaseOrderIsAlreadyReceived() {
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
        assertThrows(IllegalStateException.class, () -> receivePurchaseOrderService.receive(id));
        verify(purchaseOrderRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenPurchaseOrderIsCancelled() {
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
        assertThrows(IllegalStateException.class, () -> receivePurchaseOrderService.receive(id));
        verify(purchaseOrderRepository, never()).save(any());
    }
}
