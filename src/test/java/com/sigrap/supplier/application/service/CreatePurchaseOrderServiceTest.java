package com.sigrap.supplier.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;

import com.sigrap.supplier.application.port.in.command.CreatePurchaseOrderCommand;
import com.sigrap.supplier.domain.model.*;
import com.sigrap.supplier.domain.port.PurchaseOrderRepositoryPort;
import com.sigrap.supplier.domain.port.SupplierRepositoryPort;
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
class CreatePurchaseOrderServiceTest {
    
    @Mock
    private PurchaseOrderRepositoryPort purchaseOrderRepository;
    
    @Mock
    private SupplierRepositoryPort supplierRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;

    @InjectMocks
    private CreatePurchaseOrderService createPurchaseOrderService;
    
    @Test
    void shouldCreatePurchaseOrderWithAllFields() {
        // Given
        CreatePurchaseOrderCommand command = new CreatePurchaseOrderCommand(
            "PO-2024-001",
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            new BigDecimal("1500.00"),
            "Test notes"
        );
        
        Supplier supplier = new Supplier(
            new SupplierId(1L),
            new SupplierName("Test Supplier"),
            "Contact",
            new SupplierEmail("test@supplier.com"),
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(supplierRepository.findById(any())).thenReturn(Optional.of(supplier));
        when(purchaseOrderRepository.save(any())).thenAnswer(invocation -> {
            PurchaseOrder po = invocation.getArgument(0);
            return new PurchaseOrder(
                new PurchaseOrderId(1L),
                po.getOrderNumber(),
                po.getSupplierId(),
                po.getOrderDate(),
                po.getExpectedDeliveryDate(),
                po.getStatus(),
                po.getTotalAmount(),
                po.getNotes(),
                po.getCreatedAt(),
                po.getUpdatedAt()
            );
        });
        
        // When
        PurchaseOrder result = createPurchaseOrderService.create(command);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("PO-2024-001", result.getOrderNumber().value());
        assertEquals(1L, result.getSupplierId().value());
        assertEquals(LocalDate.of(2024, 1, 15), result.getOrderDate());
        assertEquals(LocalDate.of(2024, 2, 15), result.getExpectedDeliveryDate());
        assertEquals(PurchaseOrderStatus.PENDING, result.getStatus());
        assertEquals(new BigDecimal("1500.00"), result.getTotalAmount());
        assertEquals("Test notes", result.getNotes());
        
        verify(supplierRepository).findById(any(SupplierId.class));
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
    }
    
    @Test
    void shouldCreatePurchaseOrderWithMinimalFields() {
        // Given
        CreatePurchaseOrderCommand command = new CreatePurchaseOrderCommand(
            "PO-2024-002",
            2L,
            LocalDate.of(2024, 1, 20),
            null,
            new BigDecimal("500.00"),
            null
        );
        
        Supplier supplier = new Supplier(
            new SupplierId(2L),
            new SupplierName("Another Supplier"),
            null,
            new SupplierEmail("another@supplier.com"),
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(supplierRepository.findById(any())).thenReturn(Optional.of(supplier));
        when(purchaseOrderRepository.save(any())).thenAnswer(invocation -> {
            PurchaseOrder po = invocation.getArgument(0);
            return new PurchaseOrder(
                new PurchaseOrderId(2L),
                po.getOrderNumber(),
                po.getSupplierId(),
                po.getOrderDate(),
                po.getExpectedDeliveryDate(),
                po.getStatus(),
                po.getTotalAmount(),
                po.getNotes(),
                po.getCreatedAt(),
                po.getUpdatedAt()
            );
        });
        
        // When
        PurchaseOrder result = createPurchaseOrderService.create(command);
        
        // Then
        assertNotNull(result);
        assertEquals("PO-2024-002", result.getOrderNumber().value());
        assertNull(result.getExpectedDeliveryDate());
        assertNull(result.getNotes());
        assertEquals(PurchaseOrderStatus.PENDING, result.getStatus());
        
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
    }
    
    @Test
    void shouldThrowExceptionWhenSupplierNotFound() {
        // Given
        CreatePurchaseOrderCommand command = new CreatePurchaseOrderCommand(
            "PO-2024-003",
            999L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            new BigDecimal("1000.00"),
            "Notes"
        );
        
        when(supplierRepository.findById(any())).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createPurchaseOrderService.create(command)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(supplierRepository).findById(any(SupplierId.class));
        verify(purchaseOrderRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenOrderNumberIsNull() {
        // Given
        CreatePurchaseOrderCommand command = new CreatePurchaseOrderCommand(
            null,
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            new BigDecimal("1000.00"),
            "Notes"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createPurchaseOrderService.create(command));
        verify(purchaseOrderRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenTotalAmountIsNegative() {
        // Given
        CreatePurchaseOrderCommand command = new CreatePurchaseOrderCommand(
            "PO-2024-004",
            1L,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 2, 15),
            new BigDecimal("-100.00"),
            "Notes"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createPurchaseOrderService.create(command));
        verify(purchaseOrderRepository, never()).save(any());
    }
}

