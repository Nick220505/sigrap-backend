package com.sigrap.supplier.application.service;

import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierEmail;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.model.SupplierName;
import com.sigrap.supplier.domain.port.SupplierRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteSupplierServiceTest {
    
    @Mock
    private SupplierRepositoryPort supplierRepository;
    
    @InjectMocks
    private DeleteSupplierService deleteSupplierService;
    
    @Test
    void shouldDeleteSupplier() {
        // Given
        SupplierId id = new SupplierId(1L);
        Supplier supplier = new Supplier(
            id,
            new SupplierName("Test Supplier"),
            "Contact",
            new SupplierEmail("test@supplier.com"),
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(supplierRepository.findById(id)).thenReturn(Optional.of(supplier));
        doNothing().when(supplierRepository).deleteById(id);
        
        // When
        deleteSupplierService.delete(id);
        
        // Then
        verify(supplierRepository).findById(id);
        verify(supplierRepository).deleteById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenSupplierNotFound() {
        // Given
        SupplierId id = new SupplierId(999L);
        when(supplierRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> deleteSupplierService.delete(id)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(supplierRepository).findById(id);
        verify(supplierRepository, never()).deleteById(any());
    }
    
    @Test
    void shouldDeleteAllSuppliers() {
        // Given
        List<SupplierId> ids = Arrays.asList(
            new SupplierId(1L),
            new SupplierId(2L),
            new SupplierId(3L)
        );
        
        doNothing().when(supplierRepository).deleteAllById(ids);
        
        // When
        deleteSupplierService.deleteAll(ids);
        
        // Then
        verify(supplierRepository).deleteAllById(ids);
    }
    
    @Test
    void shouldDeleteAllWithEmptyList() {
        // Given
        List<SupplierId> ids = List.of();
        doNothing().when(supplierRepository).deleteAllById(ids);
        
        // When
        deleteSupplierService.deleteAll(ids);
        
        // Then
        verify(supplierRepository).deleteAllById(ids);
    }
}
