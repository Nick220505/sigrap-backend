package com.sigrap.customer.application.service;

import com.sigrap.customer.application.port.in.command.CreateCustomerCommand;
import com.sigrap.customer.domain.model.*;
import com.sigrap.customer.domain.port.CustomerRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateCustomerService.
 * Tests the use case implementation with mocked repository port.
 * 
 * <p>Following hexagonal architecture testing principles:
 * <ul>
 *   <li>Tests use case logic in isolation</li>
 *   <li>Mocks output ports (repository)</li>
 *   <li>No Spring context required</li>
 *   <li>Fast execution</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class CreateCustomerServiceTest {
    
    @Mock
    private CustomerRepositoryPort customerRepository;
    
    @InjectMocks
    private CreateCustomerService createCustomerService;
    
    @BeforeEach
    void setUp() {
        reset(customerRepository);
    }
    
    @Test
    void shouldCreateCustomerSuccessfully() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
            "John Doe",
            "12345678",
            "john.doe@example.com",
            "555-1234",
            "123 Main St"
        );
        
        when(customerRepository.existsByEmail(any(CustomerEmail.class))).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer cust = invocation.getArgument(0);
            return new Customer(
                new CustomerId(1L),
                cust.getFullName(),
                cust.getDocumentId(),
                cust.getEmail(),
                cust.getPhoneNumber(),
                cust.getAddress(),
                LocalDateTime.now(),
                LocalDateTime.now()
            );
        });
        
        // When
        Customer result = createCustomerService.create(command);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("John Doe", result.getFullName().value());
        assertEquals("12345678", result.getDocumentId());
        assertEquals("john.doe@example.com", result.getEmail().value());
        assertEquals("555-1234", result.getPhoneNumber().value());
        assertEquals("123 Main St", result.getAddress());
        
        verify(customerRepository).existsByEmail(any(CustomerEmail.class));
        verify(customerRepository).save(any(Customer.class));
    }
    
    @Test
    void shouldCreateCustomerWithMinimalData() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
            "Jane Smith",
            null,
            "jane@example.com",
            null,
            null
        );
        
        when(customerRepository.existsByEmail(any(CustomerEmail.class))).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer cust = invocation.getArgument(0);
            return new Customer(
                new CustomerId(2L),
                cust.getFullName(),
                cust.getDocumentId(),
                cust.getEmail(),
                cust.getPhoneNumber(),
                cust.getAddress(),
                LocalDateTime.now(),
                LocalDateTime.now()
            );
        });
        
        // When
        Customer result = createCustomerService.create(command);
        
        // Then
        assertNotNull(result);
        assertEquals("Jane Smith", result.getFullName().value());
        assertNull(result.getDocumentId());
        assertEquals("jane@example.com", result.getEmail().value());
        assertNull(result.getPhoneNumber());
        assertNull(result.getAddress());
        
        verify(customerRepository).save(any(Customer.class));
    }
    
    @Test
    void shouldCreateCustomerWithBlankPhoneNumber() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
            "Bob Johnson",
            "87654321",
            "bob@example.com",
            "   ",
            "456 Oak Ave"
        );
        
        when(customerRepository.existsByEmail(any(CustomerEmail.class))).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer cust = invocation.getArgument(0);
            return new Customer(
                new CustomerId(3L),
                cust.getFullName(),
                cust.getDocumentId(),
                cust.getEmail(),
                cust.getPhoneNumber(),
                cust.getAddress(),
                LocalDateTime.now(),
                LocalDateTime.now()
            );
        });
        
        // When
        Customer result = createCustomerService.create(command);
        
        // Then
        assertNotNull(result);
        assertNull(result.getPhoneNumber());
        
        verify(customerRepository).save(any(Customer.class));
    }
    
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
            "Duplicate User",
            "11111111",
            "existing@example.com",
            "555-9999",
            "789 Elm St"
        );
        
        when(customerRepository.existsByEmail(any(CustomerEmail.class))).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createCustomerService.create(command)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
        assertTrue(exception.getMessage().contains("existing@example.com"));
        
        verify(customerRepository, never()).save(any(Customer.class));
        verify(customerRepository).existsByEmail(any(CustomerEmail.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
            null,
            "12345678",
            "test@example.com",
            "555-1234",
            "123 Main St"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createCustomerService.create(command));
        verifyNoInteractions(customerRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
            "   ",
            "12345678",
            "test@example.com",
            "555-1234",
            "123 Main St"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createCustomerService.create(command));
        verifyNoInteractions(customerRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
            "John Doe",
            "12345678",
            null,
            "555-1234",
            "123 Main St"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createCustomerService.create(command));
        verifyNoInteractions(customerRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Given
        CreateCustomerCommand command = new CreateCustomerCommand(
            "John Doe",
            "12345678",
            "invalid-email",
            "555-1234",
            "123 Main St"
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> createCustomerService.create(command));
        verifyNoInteractions(customerRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenNameExceedsMaxLength() {
        // Given
        String longName = "A".repeat(256);
        CreateCustomerCommand command = new CreateCustomerCommand(
            longName,
            "12345678",
            "test@example.com",
            "555-1234",
            "123 Main St"
        );
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createCustomerService.create(command)
        );
        
        assertTrue(exception.getMessage().contains("exceed"));
        verifyNoInteractions(customerRepository);
    }
    
    @Test
    void shouldThrowExceptionWhenPhoneExceedsMaxLength() {
        // Given
        String longPhone = "1".repeat(21);
        CreateCustomerCommand command = new CreateCustomerCommand(
            "John Doe",
            "12345678",
            "test@example.com",
            longPhone,
            "123 Main St"
        );
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createCustomerService.create(command)
        );
        
        assertTrue(exception.getMessage().contains("exceed"));
        verifyNoInteractions(customerRepository);
    }
}
