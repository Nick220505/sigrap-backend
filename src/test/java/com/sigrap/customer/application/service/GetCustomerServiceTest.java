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
 * Unit tests for GetCustomerService.
 */
@ExtendWith(MockitoExtension.class)
class GetCustomerServiceTest {
    
    @Mock
    private CustomerRepositoryPort customerRepository;
    
    @InjectMocks
    private GetCustomerService getCustomerService;
    
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
    void shouldGetCustomerByIdSuccessfully() {
        // Given
        CustomerId id = new CustomerId(1L);
        when(customerRepository.findById(id)).thenReturn(Optional.of(testCustomer));
        
        // When
        Customer result = getCustomerService.getById(id);
        
        // Then
        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        assertEquals(testCustomer.getFullName(), result.getFullName());
        assertEquals(testCustomer.getEmail(), result.getEmail());
        
        verify(customerRepository).findById(id);
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        CustomerId id = new CustomerId(999L);
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> getCustomerService.getById(id)
        );
        
        assertTrue(exception.getMessage().contains("999"));
        assertTrue(exception.getMessage().contains("not found"));
        
        verify(customerRepository).findById(id);
    }
    
    @Test
    void shouldFindCustomerByIdReturningOptional() {
        // Given
        CustomerId id = new CustomerId(1L);
        when(customerRepository.findById(id)).thenReturn(Optional.of(testCustomer));
        
        // When
        Optional<Customer> result = getCustomerService.findById(id);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testCustomer.getId(), result.get().getId());
        
        verify(customerRepository).findById(id);
    }
    
    @Test
    void shouldReturnEmptyOptionalWhenCustomerNotFound() {
        // Given
        CustomerId id = new CustomerId(999L);
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        
        // When
        Optional<Customer> result = getCustomerService.findById(id);
        
        // Then
        assertFalse(result.isPresent());
        
        verify(customerRepository).findById(id);
    }
    
    @Test
    void shouldGetAllCustomersSuccessfully() {
        // Given
        Customer customer2 = new Customer(
            new CustomerId(2L),
            new CustomerName("Jane Smith"),
            "87654321",
            new CustomerEmail("jane@example.com"),
            new CustomerPhone("555-5678"),
            "456 Oak Ave",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        List<Customer> customers = Arrays.asList(testCustomer, customer2);
        when(customerRepository.findAll()).thenReturn(customers);
        
        // When
        List<Customer> result = getCustomerService.getAll();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testCustomer.getId(), result.get(0).getId());
        assertEquals(customer2.getId(), result.get(1).getId());
        
        verify(customerRepository).findAll();
    }
    
    @Test
    void shouldReturnEmptyListWhenNoCustomersExist() {
        // Given
        when(customerRepository.findAll()).thenReturn(List.of());
        
        // When
        List<Customer> result = getCustomerService.getAll();
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(customerRepository).findAll();
    }
}
