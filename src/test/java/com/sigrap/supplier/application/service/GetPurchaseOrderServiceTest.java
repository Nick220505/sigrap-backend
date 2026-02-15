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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPurchaseOrderServiceTest {
    
    @Mock
    private PurchaseOrderRepositoryPort purchaseOrderRepository;
    
    @InjectMocks
    private GetPurchaseOrderService getPurchaseOrderService;
    
    @Test
    void shouldGetPurchaseOrderById() {
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
        
        // When
        PurchaseOrder result = getPurchaseOrderService.getById(id);
        
        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("PO-001", result.getOrderNumber().value());
        verify(purchaseOrderRepository).findById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenPurchaseOrderNotFoundById() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(999L);
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getPurchaseOrderService.getById(id)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(purchaseOrderRepository).findById(id);
    }
    
    @Test
    void shouldFindPurchaseOrderById() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(1L);
        PurchaseOrder purchaseOrder = new PurchaseOrder(
            id,
            new PurchaseOrderNumber("PO-001"),
            new SupplierId(1L),
            LocalDate.of(2024, 1, 15),
            null,
            PurchaseOrderStatus.PENDING,
            new BigDecimal("500.00"),
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.of(purchaseOrder));
        
        // When
        Optional<PurchaseOrder> result = getPurchaseOrderService.findById(id);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(purchaseOrderRepository).findById(id);
    }
    
    @Test
    void shouldReturnEmptyWhenPurchaseOrderNotFound() {
        // Given
        PurchaseOrderId id = new PurchaseOrderId(999L);
        when(purchaseOrderRepository.findById(id)).thenReturn(Optional.empty());
        
        // When
        Optional<PurchaseOrder> result = getPurchaseOrderService.findById(id);
        
        // Then
        assertTrue(result.isEmpty());
        verify(purchaseOrderRepository).findById(id);
    }
    
    @Test
    void shouldGetAllPurchaseOrders() {
        // Given
        List<PurchaseOrder> purchaseOrders = Arrays.asList(
            new PurchaseOrder(
                new PurchaseOrderId(1L),
                new PurchaseOrderNumber("PO-001"),
                new SupplierId(1L),
                LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 2, 15),
                PurchaseOrderStatus.PENDING,
                new BigDecimal("1000.00"),
                "Notes 1",
                LocalDateTime.now(),
                LocalDateTime.now()
            ),
            new PurchaseOrder(
                new PurchaseOrderId(2L),
                new PurchaseOrderNumber("PO-002"),
                new SupplierId(2L),
                LocalDate.of(2024, 1, 20),
                LocalDate.of(2024, 2, 20),
                PurchaseOrderStatus.APPROVED,
                new BigDecimal("2000.00"),
                "Notes 2",
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        );
        
        when(purchaseOrderRepository.findAll()).thenReturn(purchaseOrders);
        
        // When
        List<PurchaseOrder> result = getPurchaseOrderService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("PO-001", result.get(0).getOrderNumber().value());
        assertEquals("PO-002", result.get(1).getOrderNumber().value());
        verify(purchaseOrderRepository).findAll();
    }
    
    @Test
    void shouldGetPurchaseOrdersBySupplierId() {
        // Given
        SupplierId supplierId = new SupplierId(1L);
        List<PurchaseOrder> purchaseOrders = Arrays.asList(
            new PurchaseOrder(
                new PurchaseOrderId(1L),
                new PurchaseOrderNumber("PO-001"),
                supplierId,
                LocalDate.of(2024, 1, 15),
                null,
                PurchaseOrderStatus.PENDING,
                new BigDecimal("1000.00"),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
            ),
            new PurchaseOrder(
                new PurchaseOrderId(2L),
                new PurchaseOrderNumber("PO-002"),
                supplierId,
                LocalDate.of(2024, 1, 20),
                null,
                PurchaseOrderStatus.APPROVED,
                new BigDecimal("1500.00"),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        );
        
        when(purchaseOrderRepository.findBySupplierId(supplierId)).thenReturn(purchaseOrders);
        
        // When
        List<PurchaseOrder> result = getPurchaseOrderService.getBySupplierId(supplierId);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(supplierId, result.get(0).getSupplierId());
        assertEquals(supplierId, result.get(1).getSupplierId());
        verify(purchaseOrderRepository).findBySupplierId(supplierId);
    }
    
    @Test
    void shouldGetPurchaseOrdersByStatus() {
        // Given
        PurchaseOrderStatus status = PurchaseOrderStatus.APPROVED;
        List<PurchaseOrder> purchaseOrders = Arrays.asList(
            new PurchaseOrder(
                new PurchaseOrderId(1L),
                new PurchaseOrderNumber("PO-001"),
                new SupplierId(1L),
                LocalDate.of(2024, 1, 15),
                null,
                status,
                new BigDecimal("1000.00"),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        );
        
        when(purchaseOrderRepository.findByStatus(status)).thenReturn(purchaseOrders);
        
        // When
        List<PurchaseOrder> result = getPurchaseOrderService.getByStatus(status);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(status, result.get(0).getStatus());
        verify(purchaseOrderRepository).findByStatus(status);
    }
    
    @Test
    void shouldReturnEmptyListWhenNoPurchaseOrders() {
        // Given
        when(purchaseOrderRepository.findAll()).thenReturn(List.of());
        
        // When
        List<PurchaseOrder> result = getPurchaseOrderService.getAll();
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(purchaseOrderRepository).findAll();
    }
}
