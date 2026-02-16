package com.sigrap.supplier.application.service;

import com.sigrap.audit.application.port.out.EventPublisherPort;

import com.sigrap.supplier.application.port.in.command.CreateSupplierCommand;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSupplierServiceTest {
    
    @Mock
    private SupplierRepositoryPort supplierRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;

    @InjectMocks
    private CreateSupplierService createSupplierService;
    
    @Test
    void shouldCreateSupplierWithAllFields() {
        // Given
        CreateSupplierCommand command = new CreateSupplierCommand(
            "ABC Supplies",
            "John Doe",
            "john@abcsupplies.com",
            "+1234567890",
            "123 Main St"
        );
        
        when(supplierRepository.existsByEmail(any())).thenReturn(false);
        when(supplierRepository.save(any())).thenAnswer(invocation -> {
            Supplier supplier = invocation.getArgument(0);
            return new Supplier(
                new SupplierId(1L),
                supplier.getName(),
                supplier.getContactName(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress(),
                supplier.getCreatedAt(),
                supplier.getUpdatedAt()
            );
        });
        
        // When
        Supplier result = createSupplierService.create(command);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("ABC Supplies", result.getName().value());
        assertEquals("John Doe", result.getContactName());
        assertEquals("john@abcsupplies.com", result.getEmail().value());
        assertEquals("+1234567890", result.getPhone().value());
        assertEquals("123 Main St", result.getAddress());
        
        verify(supplierRepository).existsByEmail(any(SupplierEmail.class));
        verify(supplierRepository).save(any(Supplier.class));
    }
    
    @Test
    void shouldCreateSupplierWithMinimalFields() {
        // Given
        CreateSupplierCommand command = new CreateSupplierCommand(
            "XYZ Corp",
            null,
            "contact@xyzcorp.com",
            null,
            null
        );
        
        when(supplierRepository.existsByEmail(any())).thenReturn(false);
        when(supplierRepository.save(any())).thenAnswer(invocation -> {
            Supplier supplier = invocation.getArgument(0);
            return new Supplier(
                new SupplierId(2L),
                supplier.getName(),
                supplier.getContactName(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress(),
                supplier.getCreatedAt(),
                supplier.getUpdatedAt()
            );
        });
        
        // When
        Supplier result = createSupplierService.create(command);
        
        // Then
        assertNotNull(result);
        assertEquals("XYZ Corp", result.getName().value());
        assertNull(result.getContactName());
        assertEquals("contact@xyzcorp.com", result.getEmail().value());
        assertNull(result.getPhone());
        assertNull(result.getAddress());
        
        verify(supplierRepository).save(any(Supplier.class));
    }
    
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        CreateSupplierCommand command = new CreateSupplierCommand(
            "Duplicate Supplier",
            "Jane Doe",
            "existing@email.com",
            "+9876543210",
            "456 Oak Ave"
        );
        
        when(supplierRepository.existsByEmail(any())).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createSupplierService.create(command)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(supplierRepository).existsByEmail(any(SupplierEmail.class));
        verify(supplierRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        // Given
        CreateSupplierCommand command = new CreateSupplierCommand(
            null,
            "Contact",
            "test@test.com",
            "+1234567890",
            "Address"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createSupplierService.create(command));
        verify(supplierRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        // Given
        CreateSupplierCommand command = new CreateSupplierCommand(
            "   ",
            "Contact",
            "test@test.com",
            "+1234567890",
            "Address"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createSupplierService.create(command));
        verify(supplierRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        // Given
        CreateSupplierCommand command = new CreateSupplierCommand(
            "Valid Name",
            "Contact",
            null,
            "+1234567890",
            "Address"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createSupplierService.create(command));
        verify(supplierRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Given
        CreateSupplierCommand command = new CreateSupplierCommand(
            "Valid Name",
            "Contact",
            "invalid-email",
            "+1234567890",
            "Address"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createSupplierService.create(command));
        verify(supplierRepository, never()).save(any());
    }
    
    @Test
    void shouldCreateSupplierWithEmptyPhone() {
        // Given
        CreateSupplierCommand command = new CreateSupplierCommand(
            "Test Supplier",
            "Contact",
            "test@supplier.com",
            "",
            "Address"
        );
        
        when(supplierRepository.existsByEmail(any())).thenReturn(false);
        when(supplierRepository.save(any())).thenAnswer(invocation -> {
            Supplier supplier = invocation.getArgument(0);
            return new Supplier(
                new SupplierId(3L),
                supplier.getName(),
                supplier.getContactName(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress(),
                supplier.getCreatedAt(),
                supplier.getUpdatedAt()
            );
        });
        
        // When
        Supplier result = createSupplierService.create(command);
        
        // Then
        assertNotNull(result);
        assertNull(result.getPhone());
        verify(supplierRepository).save(any(Supplier.class));
    }
}

