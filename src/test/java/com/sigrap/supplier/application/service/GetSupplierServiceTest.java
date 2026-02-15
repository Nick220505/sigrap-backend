package com.sigrap.supplier.application.service;

import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierEmail;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.model.SupplierName;
import com.sigrap.supplier.domain.model.SupplierPhone;
import com.sigrap.supplier.domain.port.SupplierRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSupplierServiceTest {
    
    @Mock
    private SupplierRepositoryPort supplierRepository;
    
    @InjectMocks
    private GetSupplierService getSupplierService;
    
    @Test
    void shouldGetSupplierById() {
        // Given
        SupplierId id = new SupplierId(1L);
        Supplier supplier = new Supplier(
            id,
            new SupplierName("Test Supplier"),
            "Contact",
            new SupplierEmail("test@supplier.com"),
            new SupplierPhone("+1234567890"),
            "Address",
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );
        
        when(supplierRepository.findById(id)).thenReturn(Optional.of(supplier));
        
        // When
        Supplier result = getSupplierService.getById(id);
        
        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Test Supplier", result.getName().value());
        verify(supplierRepository).findById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenSupplierNotFoundById() {
        // Given
        SupplierId id = new SupplierId(999L);
        when(supplierRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> getSupplierService.getById(id)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(supplierRepository).findById(id);
    }
    
    @Test
    void shouldFindSupplierById() {
        // Given
        SupplierId id = new SupplierId(1L);
        Supplier supplier = new Supplier(
            id,
            new SupplierName("Test Supplier"),
            "Contact",
            new SupplierEmail("test@supplier.com"),
            null,
            null,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );
        
        when(supplierRepository.findById(id)).thenReturn(Optional.of(supplier));
        
        // When
        Optional<Supplier> result = getSupplierService.findById(id);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(supplierRepository).findById(id);
    }
    
    @Test
    void shouldReturnEmptyWhenSupplierNotFound() {
        // Given
        SupplierId id = new SupplierId(999L);
        when(supplierRepository.findById(id)).thenReturn(Optional.empty());
        
        // When
        Optional<Supplier> result = getSupplierService.findById(id);
        
        // Then
        assertTrue(result.isEmpty());
        verify(supplierRepository).findById(id);
    }
    
    @Test
    void shouldGetAllSuppliers() {
        // Given
        List<Supplier> suppliers = Arrays.asList(
            new Supplier(
                new SupplierId(1L),
                new SupplierName("Supplier 1"),
                "Contact 1",
                new SupplierEmail("supplier1@test.com"),
                new SupplierPhone("+1111111111"),
                "Address 1",
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
            ),
            new Supplier(
                new SupplierId(2L),
                new SupplierName("Supplier 2"),
                "Contact 2",
                new SupplierEmail("supplier2@test.com"),
                new SupplierPhone("+2222222222"),
                "Address 2",
                java.time.LocalDateTime.now(),
                java.time.LocalDateTime.now()
            )
        );
        
        when(supplierRepository.findAll()).thenReturn(suppliers);
        
        // When
        List<Supplier> result = getSupplierService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Supplier 1", result.get(0).getName().value());
        assertEquals("Supplier 2", result.get(1).getName().value());
        verify(supplierRepository).findAll();
    }
    
    @Test
    void shouldReturnEmptyListWhenNoSuppliers() {
        // Given
        when(supplierRepository.findAll()).thenReturn(List.of());
        
        // When
        List<Supplier> result = getSupplierService.getAll();
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(supplierRepository).findAll();
    }
}
