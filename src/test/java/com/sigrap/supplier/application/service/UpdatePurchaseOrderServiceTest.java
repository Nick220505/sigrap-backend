package com.sigrap.supplier.application.service;

import com.sigrap.supplier.application.port.in.command.UpdatePurchaseOrderCommand;
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
class UpdatePurchaseOrderServiceTest {
    
    @Mock
    private PurchaseOrderRepositoryPort purchaseOrderRepository;
    
    @InjectMocks
    private UpdatePurchaseOrderService updatePurchaseOrderService;
    
    @Test
    void shouldUpdatePurchaseOrderWhenPending() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(1L);
        PurchaseOrder existingPO = new PurchaseOrder(
            id,
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            PurchaseOrderStatus.PENDING,
            new BigDecimal("1000.00"),
            "Old notes",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        UpdatePurchaseOrderCommand command = new UpdatePurchaseOrderCommand(
            LocalDate.of(2024, 3, 1),
            "Updated notes"
        );
        
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(existingPO));
        when(purchaseOrderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        PurchaseOrder result = updatePurchaseOrderService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 3, 1), result.getExpectedDeliveryDate());
        assertEquals("Updated notes", result.getNotes());
        
        verify(purchaseOrderRepository).findById(id);
        verify(purchaseOrderRepository).save(existingPO);
    }
    
    @Test
    void shouldUpdatePurchaseOrderWhenApproved() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(1L);
        PurchaseOrder existingPO = new PurchaseOrder(
            id,
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            PurchaseOrderStatus.APPROVED,
            new BigDecimal("1000.00"),
            "Old notes",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        UpdatePurchaseOrderCommand command = new UpdatePurchaseOrderCommand(
            LocalDate.of(2024, 3, 1),
            "Updated notes"
        );
        
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(existingPO));
        when(purchaseOrderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        PurchaseOrder result = updatePurchaseOrderService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 3, 1), result.getExpectedDeliveryDate());
        verify(purchaseOrderRepository).save(existingPO);
    }
    
    @Test
    void shouldThrowExceptionWhenPurchaseOrderNotFound() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(999L);
        UpdatePurchaseOrderCommand command = new UpdatePurchaseOrderCommand(
            LocalDate.of(2024, 3, 1),
            "Notes"
        );
        
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> updatePurchaseOrderService.update(id, command)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(purchaseOrderRepository).findById(id);
        verify(purchaseOrderRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenPurchaseOrderIsReceived() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(1L);
        PurchaseOrder existingPO = new PurchaseOrder(
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
        
        UpdatePurchaseOrderCommand command = new UpdatePurchaseOrderCommand(
            LocalDate.of(2024, 3, 1),
            "Updated notes"
        );
        
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(existingPO));
        
        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> updatePurchaseOrderService.update(id, command)
        );
        
        assertTrue(exception.getMessage().contains("cannot be modified"));
        verify(purchaseOrderRepository).findById(id);
        verify(purchaseOrderRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenPurchaseOrderIsCancelled() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(1L);
        PurchaseOrder existingPO = new PurchaseOrder(
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
        
        UpdatePurchaseOrderCommand command = new UpdatePurchaseOrderCommand(
            LocalDate.of(2024, 3, 1),
            "Updated notes"
        );
        
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(existingPO));
        
        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> updatePurchaseOrderService.update(id, command)
        );
        
        assertTrue(exception.getMessage().contains("cannot be modified"));
        verify(purchaseOrderRepository, never()).save(any());
    }
    
    @Test
    void shouldUpdateWithNullFields() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(1L);
        PurchaseOrder existingPO = new PurchaseOrder(
            id,
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            PurchaseOrderStatus.PENDING,
            new BigDecimal("1000.00"),
            "Old notes",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        UpdatePurchaseOrderCommand command = new UpdatePurchaseOrderCommand(null, null);
        
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(existingPO));
        when(purchaseOrderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        PurchaseOrder result = updatePurchaseOrderService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertNull(result.getExpectedDeliveryDate());
        assertNull(result.getNotes());
        verify(purchaseOrderRepository).save(existingPO);
    }
}
