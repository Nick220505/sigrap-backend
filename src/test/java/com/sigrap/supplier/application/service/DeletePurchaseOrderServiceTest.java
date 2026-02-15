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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePurchaseOrderServiceTest {
    
    @Mock
    private PurchaseOrderRepositoryPort purchaseOrderRepository;
    
    @InjectMocks
    private DeletePurchaseOrderService deletePurchaseOrderService;
    
    @Test
    void shouldDeletePurchaseOrder() {
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
        doNothing().when(purchaseOrderRepository).deleteById(id);
        
        // When
        deletePurchaseOrderService.delete(id);
        
        // Then
        verify(purchaseOrderRepository).findById(id);
        verify(purchaseOrderRepository).deleteById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenPurchaseOrderNotFound() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(999L);
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> deletePurchaseOrderService.delete(id)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(purchaseOrderRepository).findById(id);
        verify(purchaseOrderRepository, never()).deleteById(any());
    }
}
