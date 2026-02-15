package com.sigrap.customer.application.service;

import com.sigrap.customer.application.port.in.command.UpdateCustomerCommand;
import com.sigrap.customer.domain.model.*;
import com.sigrap.customer.domain.port.CustomerRepositoryPort;
import com.sigrap.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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

/**
 * Unit tests for UpdateCustomerService.
 */
@ExtendWith(MockitoExtension.class)
class UpdateCustomerServiceTest {
    
    @Mock
    private CustomerRepositoryPort customerRepository;
    
    @InjectMocks
    private UpdateCustomerService updateCustomerService;
    
    private Customer existingCustomer;
    
    @BeforeEach
    void setUp() {
        reset(customerRepository);
        
        existingCustomer = new Customer(
            new CustomerId(1L),
            new CustomerName("John Doe"),
            "12345678",
            new CustomerEmail("john@example.com"),
            new CustomerPhone("555-1234"),
            "123 Main St",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1)
        );
    }
    
    @Test
    void shouldUpdateCustomerSuccessfully() {
        // Given
        CustomerId id = new CustomerId(1L);
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            "John Updated",
            "87654321",
            "john.updated@example.com",
            "555-9999",
            "789 New St"
        );
        
        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByEmailAndIdNot(any(CustomerEmail.class), eq(id))).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Customer result = updateCustomerService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertEquals("John Updated", result.getFullName().value());
        assertEquals("87654321", result.getDocumentId());
        assertEquals("john.updated@example.com", result.getEmail().value());
        assertEquals("555-9999", result.getPhoneNumber().value());
        assertEquals("789 New St", result.getAddress());
        
        verify(customerRepository).findById(id);
        verify(customerRepository).existsByEmailAndIdNot(any(CustomerEmail.class), eq(id));
        verify(customerRepository).save(any(Customer.class));
    }
    
    @Test
    void shouldUpdateCustomerWithSameEmail() {
        // Given
        CustomerId id = new CustomerId(1L);
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            "John Updated",
            "87654321",
            "john@example.com", // Same email
            "555-9999",
            "789 New St"
        );
        
        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Customer result = updateCustomerService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail().value());
        
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).existsByEmailAndIdNot(any(), any());
        verify(customerRepository).save(any(Customer.class));
    }
    
    @Test
    void shouldUpdateCustomerWithNullOptionalFields() {
        // Given
        CustomerId id = new CustomerId(1L);
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            "John Updated",
            null,
            "john@example.com",
            null,
            null
        );
        
        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        Customer result = updateCustomerService.update(id, command);
        
        // Then
        assertNotNull(result);
        assertNull(result.getDocumentId());
        assertNull(result.getPhoneNumber());
        assertNull(result.getAddress());
        
        verify(customerRepository).save(any(Customer.class));
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        CustomerId id = new CustomerId(999L);
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            "John Updated",
            "87654321",
            "john.updated@example.com",
            "555-9999",
            "789 New St"
        );
        
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> updateCustomerService.update(id, command)
        );
        
        assertTrue(exception.getMessage().contains("999"));
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).save(any(Customer.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNewEmailAlreadyExists() {
        // Given
        CustomerId id = new CustomerId(1L);
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            "John Updated",
            "87654321",
            "existing@example.com",
            "555-9999",
            "789 New St"
        );
        
        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByEmailAndIdNot(any(CustomerEmail.class), eq(id))).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> updateCustomerService.update(id, command)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        assertTrue(exception.getMessage().contains("existing@example.com"));
        
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).save(any(Customer.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNewNameIsInvalid() {
        // Given
        CustomerId id = new CustomerId(1L);
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            "   ",
            "87654321",
            "john@example.com",
            "555-9999",
            "789 New St"
        );
        
        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> updateCustomerService.update(id, command));
        
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).save(any(Customer.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNewEmailIsInvalid() {
        // Given
        CustomerId id = new CustomerId(1L);
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            "John Updated",
            "87654321",
            "invalid-email",
            "555-9999",
            "789 New St"
        );
        
        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> updateCustomerService.update(id, command));
        
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
