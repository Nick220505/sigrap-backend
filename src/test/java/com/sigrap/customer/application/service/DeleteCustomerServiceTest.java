package com.sigrap.customer.application.service;

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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DeleteCustomerService.
 */
@ExtendWith(MockitoExtension.class)
class DeleteCustomerServiceTest {
    
    @Mock
    private CustomerRepositoryPort customerRepository;
    
    @InjectMocks
    private DeleteCustomerService deleteCustomerService;
    
    private Customer testCustomer;
    
    @BeforeEach
    void setUp() {
        reset(customerRepository);
        
        testCustomer = new Customer(
            new CustomerId(1L),
            new CustomerName("John Doe"),
            "12345678",
            new CustomerEmail("john@example.com"),
            new CustomerPhone("555-1234"),
            "123 Main St",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    @Test
    void shouldDeleteCustomerSuccessfully() {
        // Given
        CustomerId id = new CustomerId(1L);
        when(customerRepository.findById(id)).thenReturn(Optional.of(testCustomer));
        doNothing().when(customerRepository).deleteById(id);
        
        // When
        deleteCustomerService.delete(id);
        
        // Then
        verify(customerRepository).findById(id);
        verify(customerRepository).deleteById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerNotFoundForDelete() {
        // Given
        CustomerId id = new CustomerId(999L);
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> deleteCustomerService.delete(id)
        );
        
        assertTrue(exception.getMessage().contains("999"));
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).deleteById(any());
    }
    
    @Test
    void shouldDeleteAllCustomersSuccessfully() {
        // Given
        CustomerId id1 = new CustomerId(1L);
        CustomerId id2 = new CustomerId(2L);
        List<CustomerId> ids = Arrays.asList(id1, id2);
        
        Customer customer2 = new Customer(
            id2,
            new CustomerName("Jane Smith"),
            "87654321",
            new CustomerEmail("jane@example.com"),
            new CustomerPhone("555-5678"),
            "456 Oak Ave",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        when(customerRepository.findById(id1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.findById(id2)).thenReturn(Optional.of(customer2));
        doNothing().when(customerRepository).deleteAllById(ids);
        
        // When
        deleteCustomerService.deleteAll(ids);
        
        // Then
        verify(customerRepository).findById(id1);
        verify(customerRepository).findById(id2);
        verify(customerRepository).deleteAllById(ids);
    }
    
    @Test
    void shouldThrowExceptionWhenAnyCustomerNotFoundForDeleteAll() {
        // Given
        CustomerId id1 = new CustomerId(1L);
        CustomerId id2 = new CustomerId(999L);
        List<CustomerId> ids = Arrays.asList(id1, id2);
        
        when(customerRepository.findById(id1)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.findById(id2)).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> deleteCustomerService.deleteAll(ids)
        );
        
        assertTrue(exception.getMessage().contains("999"));
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(customerRepository).findById(id1);
        verify(customerRepository).findById(id2);
        verify(customerRepository, never()).deleteAllById(any());
    }
    
    @Test
    void shouldDeleteAllWithEmptyList() {
        // Given
        List<CustomerId> ids = List.of();
        doNothing().when(customerRepository).deleteAllById(ids);
        
        // When
        deleteCustomerService.deleteAll(ids);
        
        // Then
        verify(customerRepository, never()).findById(any());
        verify(customerRepository).deleteAllById(ids);
    }
}
