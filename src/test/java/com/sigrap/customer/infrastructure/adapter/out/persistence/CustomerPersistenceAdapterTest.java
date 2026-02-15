package com.sigrap.customer.infrastructure.adapter.out.persistence;

import com.sigrap.customer.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CustomerPersistenceAdapter.
 * Tests the adapter with a real database (H2) using Spring Boot test context.
 * 
 * These tests verify:
 * - Correct mapping between domain and JPA entities
 * - Database operations work as expected
 * - The adapter properly implements the repository port
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomerPersistenceAdapterTest {

    @Autowired
    private CustomerPersistenceAdapter adapter;

    @Autowired
    private CustomerJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewCustomer() {
        // Given
        Customer customer = new Customer(
            new CustomerName("John Doe"),
            "12345678",
            new CustomerEmail("john.doe@example.com"),
            new CustomerPhone("555-1234"),
            "123 Main St"
        );

        // When
        Customer saved = adapter.save(customer);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("John Doe", saved.getFullName().value());
        assertEquals("12345678", saved.getDocumentId());
        assertEquals("john.doe@example.com", saved.getEmail().value());
        assertEquals("555-1234", saved.getPhoneNumber().value());
        assertEquals("123 Main St", saved.getAddress());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldUpdateExistingCustomer() {
        // Given - save initial customer
        Customer customer = new Customer(
            new CustomerName("Jane Smith"),
            "87654321",
            new CustomerEmail("jane.smith@example.com"),
            new CustomerPhone("555-5678"),
            "456 Oak Ave"
        );
        Customer saved = adapter.save(customer);
        
        // When - update the customer
        Customer updated = new Customer(
            saved.getId(),
            new CustomerName("Jane Smith-Johnson"),
            "87654321",
            new CustomerEmail("jane.johnson@example.com"),
            new CustomerPhone("555-9999"),
            "789 Pine Rd",
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );
        Customer result = adapter.save(updated);

        // Then
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Jane Smith-Johnson", result.getFullName().value());
        assertEquals("jane.johnson@example.com", result.getEmail().value());
        assertEquals("555-9999", result.getPhoneNumber().value());
        assertEquals("789 Pine Rd", result.getAddress());
    }

    @Test
    void shouldFindCustomerById() {
        // Given
        Customer customer = new Customer(
            new CustomerName("Bob Wilson"),
            "11223344",
            new CustomerEmail("bob.wilson@example.com"),
            new CustomerPhone("555-1111"),
            "321 Elm St"
        );
        Customer saved = adapter.save(customer);

        // When
        Optional<Customer> found = adapter.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Bob Wilson", found.get().getFullName().value());
        assertEquals("bob.wilson@example.com", found.get().getEmail().value());
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFound() {
        // Given
        CustomerId nonExistentId = new CustomerId(999L);

        // When
        Optional<Customer> found = adapter.findById(nonExistentId);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllCustomers() {
        // Given
        adapter.save(new Customer(
            new CustomerName("Customer 1"),
            "DOC1",
            new CustomerEmail("customer1@example.com"),
            new CustomerPhone("555-0001"),
            "Address 1"
        ));
        adapter.save(new Customer(
            new CustomerName("Customer 2"),
            "DOC2",
            new CustomerEmail("customer2@example.com"),
            new CustomerPhone("555-0002"),
            "Address 2"
        ));
        adapter.save(new Customer(
            new CustomerName("Customer 3"),
            "DOC3",
            new CustomerEmail("customer3@example.com"),
            null,
            null
        ));

        // When
        List<Customer> customers = adapter.findAll();

        // Then
        assertNotNull(customers);
        assertEquals(3, customers.size());
        assertTrue(customers.stream().anyMatch(c -> c.getFullName().value().equals("Customer 1")));
        assertTrue(customers.stream().anyMatch(c -> c.getFullName().value().equals("Customer 2")));
        assertTrue(customers.stream().anyMatch(c -> c.getFullName().value().equals("Customer 3")));
    }

    @Test
    void shouldReturnEmptyListWhenNoCustomers() {
        // When
        List<Customer> customers = adapter.findAll();

        // Then
        assertNotNull(customers);
        assertTrue(customers.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        // Given
        adapter.save(new Customer(
            new CustomerName("Alice Brown"),
            "99887766",
            new CustomerEmail("alice.brown@example.com"),
            new CustomerPhone("555-2222"),
            "654 Maple Dr"
        ));

        // When
        boolean exists = adapter.existsByEmail(new CustomerEmail("alice.brown@example.com"));

        // Then
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // Given
        CustomerEmail nonExistentEmail = new CustomerEmail("nonexistent@example.com");

        // When
        boolean exists = adapter.existsByEmail(nonExistentEmail);

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldReturnTrueWhenEmailExistsForDifferentId() {
        // Given
        Customer customer1 = adapter.save(new Customer(
            new CustomerName("Customer One"),
            "DOC1",
            new CustomerEmail("duplicate@example.com"),
            new CustomerPhone("555-1111"),
            "Address 1"
        ));
        
        adapter.save(new Customer(
            new CustomerName("Customer Two"),
            "DOC2",
            new CustomerEmail("another@example.com"),
            new CustomerPhone("555-2222"),
            "Address 2"
        ));

        // When
        boolean exists = adapter.existsByEmailAndIdNot(
            new CustomerEmail("another@example.com"),
            customer1.getId()
        );

        // Then
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailOnlyExistsForExcludedId() {
        // Given
        Customer customer = adapter.save(new Customer(
            new CustomerName("Unique Customer"),
            "UNIQUE",
            new CustomerEmail("unique@example.com"),
            new CustomerPhone("555-9999"),
            "Unique Address"
        ));

        // When
        boolean exists = adapter.existsByEmailAndIdNot(
            new CustomerEmail("unique@example.com"),
            customer.getId()
        );

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldDeleteCustomerById() {
        // Given
        Customer customer = new Customer(
            new CustomerName("Temporary Customer"),
            "TEMP",
            new CustomerEmail("temp@example.com"),
            new CustomerPhone("555-0000"),
            "Temp Address"
        );
        Customer saved = adapter.save(customer);
        CustomerId savedId = saved.getId();

        // When
        adapter.deleteById(savedId);

        // Then
        Optional<Customer> found = adapter.findById(savedId);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldDeleteMultipleCustomersById() {
        // Given
        Customer cust1 = adapter.save(new Customer(
            new CustomerName("Customer A"),
            "DOCA",
            new CustomerEmail("customerA@example.com"),
            new CustomerPhone("555-1111"),
            "Address A"
        ));
        Customer cust2 = adapter.save(new Customer(
            new CustomerName("Customer B"),
            "DOCB",
            new CustomerEmail("customerB@example.com"),
            new CustomerPhone("555-2222"),
            "Address B"
        ));
        Customer cust3 = adapter.save(new Customer(
            new CustomerName("Customer C"),
            "DOCC",
            new CustomerEmail("customerC@example.com"),
            new CustomerPhone("555-3333"),
            "Address C"
        ));
        
        List<CustomerId> idsToDelete = List.of(cust1.getId(), cust2.getId());

        // When
        adapter.deleteAllById(idsToDelete);

        // Then
        assertTrue(adapter.findById(cust1.getId()).isEmpty());
        assertTrue(adapter.findById(cust2.getId()).isEmpty());
        assertTrue(adapter.findById(cust3.getId()).isPresent());
    }

    @Test
    void shouldPreserveTimestampsWhenSaving() {
        // Given
        Customer customer = new Customer(
            new CustomerName("Test Customer"),
            "TEST123",
            new CustomerEmail("test@example.com"),
            new CustomerPhone("555-TEST"),
            "Test Address"
        );

        // When
        Customer saved = adapter.save(customer);

        // Then
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldHandleCustomerWithNullOptionalFields() {
        // Given
        Customer customer = new Customer(
            new CustomerName("Minimal Customer"),
            null,
            new CustomerEmail("minimal@example.com"),
            null,
            null
        );

        // When
        Customer saved = adapter.save(customer);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Minimal Customer", saved.getFullName().value());
        assertNull(saved.getDocumentId());
        assertEquals("minimal@example.com", saved.getEmail().value());
        assertNull(saved.getPhoneNumber());
        assertNull(saved.getAddress());
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        // Given
        Customer customer = new Customer(
            new CustomerName("Complete Customer"),
            "COMPLETE123",
            new CustomerEmail("complete@example.com"),
            new CustomerPhone("555-COMPLETE"),
            "123 Complete Street, City, State 12345"
        );

        // When
        Customer saved = adapter.save(customer);
        Optional<Customer> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        Customer result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(saved.getFullName().value(), result.getFullName().value());
        assertEquals(saved.getDocumentId(), result.getDocumentId());
        assertEquals(saved.getEmail().value(), result.getEmail().value());
        assertEquals(saved.getPhoneNumber().value(), result.getPhoneNumber().value());
        assertEquals(saved.getAddress(), result.getAddress());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void shouldEnforceUniqueEmailConstraint() {
        // Given
        adapter.save(new Customer(
            new CustomerName("First Customer"),
            "FIRST",
            new CustomerEmail("unique@example.com"),
            new CustomerPhone("555-1111"),
            "First Address"
        ));

        // When & Then
        assertThrows(Exception.class, () -> {
            adapter.save(new Customer(
                new CustomerName("Second Customer"),
                "SECOND",
                new CustomerEmail("unique@example.com"),
                new CustomerPhone("555-2222"),
                "Second Address"
            ));
        });
    }

    @Test
    void shouldHandleCustomerWithLongAddress() {
        // Given
        String longAddress = "A".repeat(255); // Max length based on actual schema
        Customer customer = new Customer(
            new CustomerName("Long Address Customer"),
            "LONG",
            new CustomerEmail("longaddress@example.com"),
            new CustomerPhone("555-LONG"),
            longAddress
        );

        // When
        Customer saved = adapter.save(customer);
        Optional<Customer> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(longAddress, retrieved.get().getAddress());
    }

    @Test
    void shouldHandleCustomerWithSpecialCharactersInName() {
        // Given
        Customer customer = new Customer(
            new CustomerName("José María O'Brien-Smith"),
            "SPECIAL",
            new CustomerEmail("special@example.com"),
            new CustomerPhone("555-SPEC"),
            "123 Special St"
        );

        // When
        Customer saved = adapter.save(customer);
        Optional<Customer> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals("José María O'Brien-Smith", retrieved.get().getFullName().value());
    }
}
