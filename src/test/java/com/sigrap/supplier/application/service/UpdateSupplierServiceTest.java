package com.sigrap.supplier.application.service;

import com.sigrap.supplier.application.port.in.command.UpdateSupplierCommand;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSupplierServiceTest {
    
    @Mock
    private SupplierRepositoryPort supplierRepository;
    
    @InjectMocks
    private UpdateSupplierService updateSupplierService;
    
    @Test
    void shouldUpdateSupplierWithAllFields() {
        // Given
        SupplierId id = new SupplierId(1L);
        Supplier existingSupplier = new Supplier(
            id,
            new SupplierName("Old Name"),
            "Old Contact",
            new SupplierEmail("old@email.com"),
            new SupplierPhone("+1111111111"),
            "Old Address",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        UpdateSupplierCommand command = new UpdateSupplierCommand(
            "New Name",
            "New Contact",
            "new@email.com",
            "+2222222222",
            "New Address"
        );
        
        when(supplierRepository.findById(id)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.existsByEmailAndIdNot(any(), any())).thenReturn(false);
        when(supplierRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Supplier result = updateSupplierService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertEquals("New Name", result.getName().value());
        assertEquals("New Contact", result.getContactName());
        assertEquals("new@email.com", result.getEmail().value());
        assertEquals("+2222222222", result.getPhone().value());
        assertEquals("New Address", result.getAddress());
        
        verify(supplierRepository).findById(id);
        verify(supplierRepository).existsByEmailAndIdNot(any(SupplierEmail.class), eq(id));
        verify(supplierRepository).save(existingSupplier);
    }
    
    @Test
    void shouldUpdateSupplierWithSameEmail() {
        // Given
        SupplierId id = new SupplierId(1L);
        Supplier existingSupplier = new Supplier(
            id,
            new SupplierName("Test Supplier"),
            "Contact",
            new SupplierEmail("same@email.com"),
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        UpdateSupplierCommand command = new UpdateSupplierCommand(
            "Updated Name",
            "Updated Contact",
            "same@email.com",
            "+1234567890",
            "Updated Address"
        );
        
        when(supplierRepository.findById(id)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Supplier result = updateSupplierService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.getName().value());
        verify(supplierRepository).findById(id);
        verify(supplierRepository, never()).existsByEmailAndIdNot(any(), any());
        verify(supplierRepository).save(existingSupplier);
    }
    
    @Test
    void shouldThrowExceptionWhenSupplierNotFound() {
        // Given
        SupplierId id = new SupplierId(999L);
        UpdateSupplierCommand command = new UpdateSupplierCommand(
            "Name",
            "Contact",
            "email@test.com",
            "+1234567890",
            "Address"
        );
        
        when(supplierRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> updateSupplierService.update(id, command)
        );
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(supplierRepository).findById(id);
        verify(supplierRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExistsForAnotherSupplier() {
        // Given
        SupplierId id = new SupplierId(1L);
        Supplier existingSupplier = new Supplier(
            id,
            new SupplierName("Test Supplier"),
            "Contact",
            new SupplierEmail("old@email.com"),
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        UpdateSupplierCommand command = new UpdateSupplierCommand(
            "Updated Name",
            "Contact",
            "existing@email.com",
            "+1234567890",
            "Address"
        );
        
        when(supplierRepository.findById(id)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.existsByEmailAndIdNot(any(), any())).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> updateSupplierService.update(id, command)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(supplierRepository).findById(id);
        verify(supplierRepository).existsByEmailAndIdNot(any(SupplierEmail.class), eq(id));
        verify(supplierRepository, never()).save(any());
    }
    
    @Test
    void shouldUpdateSupplierWithNullOptionalFields() {
        // Given
        SupplierId id = new SupplierId(1L);
        Supplier existingSupplier = new Supplier(
            id,
            new SupplierName("Test Supplier"),
            "Old Contact",
            new SupplierEmail("test@email.com"),
            new SupplierPhone("+1111111111"),
            "Old Address",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        UpdateSupplierCommand command = new UpdateSupplierCommand(
            "Updated Name",
            null,
            "test@email.com",
            null,
            null
        );
        
        when(supplierRepository.findById(id)).thenReturn(Optional.of(existingSupplier));
        when(supplierRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Supplier result = updateSupplierService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.getName().value());
        assertNull(result.getContactName());
        assertNull(result.getPhone());
        assertNull(result.getAddress());
        verify(supplierRepository).save(existingSupplier);
    }
}
