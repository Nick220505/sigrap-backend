package com.sigrap.customer.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void shouldCreateCustomerWithValidData() {
        // Given
        CustomerId id = new CustomerId(1L);
        CustomerName fullName = new CustomerName("John Doe");
        String documentId = "12345678";
        CustomerEmail email = new CustomerEmail("john.doe@example.com");
        CustomerPhone phoneNumber = new CustomerPhone("+1234567890");
        String address = "123 Main St";
        LocalDateTime now = LocalDateTime.now();

        // When
        Customer customer = new Customer(id, fullName, documentId, email, phoneNumber, address, now, now);

        // Then
        assertEquals(id, customer.getId());
        assertEquals(fullName, customer.getFullName());
        assertEquals(documentId, customer.getDocumentId());
        assertEquals(email, customer.getEmail());
        assertEquals(phoneNumber, customer.getPhoneNumber());
        assertEquals(address, customer.getAddress());
        assertEquals(now, customer.getCreatedAt());
        assertEquals(now, customer.getUpdatedAt());
    }

    @Test
    void shouldCreateNewCustomerWithoutId() {
        // Given
        CustomerName fullName = new CustomerName("John Doe");
        CustomerEmail email = new CustomerEmail("john.doe@example.com");
        CustomerPhone phoneNumber = new CustomerPhone("+1234567890");

        // When
        Customer customer = new Customer(fullName, "12345678", email, phoneNumber, "123 Main St");

        // Then
        assertNull(customer.getId());
        assertEquals(fullName, customer.getFullName());
        assertEquals(email, customer.getEmail());
        assertNotNull(customer.getCreatedAt());
        assertNotNull(customer.getUpdatedAt());
    }

    @Test
    void shouldCreateCustomerWithOptionalFieldsNull() {
        // Given
        CustomerName fullName = new CustomerName("John Doe");
        CustomerEmail email = new CustomerEmail("john.doe@example.com");

        // When
        Customer customer = new Customer(fullName, null, email, null, null);

        // Then
        assertNull(customer.getDocumentId());
        assertNull(customer.getPhoneNumber());
        assertNull(customer.getAddress());
    }

    @Test
    void shouldThrowExceptionWhenFullNameIsNull() {
        // Given
        CustomerEmail email = new CustomerEmail("john.doe@example.com");

        // When/Then
        assertThrows(NullPointerException.class, () ->
            new Customer(null, "12345678", email, null, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        // Given
        CustomerName fullName = new CustomerName("John Doe");

        // When/Then
        assertThrows(NullPointerException.class, () ->
            new Customer(fullName, "12345678", null, null, null)
        );
    }

    @Test
    void shouldUpdateFullName() {
        // Given
        Customer customer = createTestCustomer();
        CustomerName newFullName = new CustomerName("Jane Smith");
        LocalDateTime originalUpdatedAt = customer.getUpdatedAt();

        // When
        customer.updateFullName(newFullName);

        // Then
        assertEquals(newFullName, customer.getFullName());
        assertTrue(customer.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldNotUpdateTimestampWhenFullNameIsUnchanged() {
        // Given
        Customer customer = createTestCustomer();
        CustomerName sameName = new CustomerName("John Doe");
        LocalDateTime originalUpdatedAt = customer.getUpdatedAt();

        // When
        customer.updateFullName(sameName);

        // Then
        assertEquals(originalUpdatedAt, customer.getUpdatedAt());
    }

    @Test
    void shouldUpdateDocumentId() {
        // Given
        Customer customer = createTestCustomer();
        String newDocumentId = "87654321";
        LocalDateTime originalUpdatedAt = customer.getUpdatedAt();

        // When
        customer.updateDocumentId(newDocumentId);

        // Then
        assertEquals(newDocumentId, customer.getDocumentId());
        assertTrue(customer.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldUpdateEmail() {
        // Given
        Customer customer = createTestCustomer();
        CustomerEmail newEmail = new CustomerEmail("jane.smith@example.com");
        LocalDateTime originalUpdatedAt = customer.getUpdatedAt();

        // When
        customer.updateEmail(newEmail);

        // Then
        assertEquals(newEmail, customer.getEmail());
        assertTrue(customer.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldNotUpdateTimestampWhenEmailIsUnchanged() {
        // Given
        Customer customer = createTestCustomer();
        CustomerEmail sameEmail = new CustomerEmail("john.doe@example.com");
        LocalDateTime originalUpdatedAt = customer.getUpdatedAt();

        // When
        customer.updateEmail(sameEmail);

        // Then
        assertEquals(originalUpdatedAt, customer.getUpdatedAt());
    }

    @Test
    void shouldUpdatePhoneNumber() {
        // Given
        Customer customer = createTestCustomer();
        CustomerPhone newPhoneNumber = new CustomerPhone("+0987654321");
        LocalDateTime originalUpdatedAt = customer.getUpdatedAt();

        // When
        customer.updatePhoneNumber(newPhoneNumber);

        // Then
        assertEquals(newPhoneNumber, customer.getPhoneNumber());
        assertTrue(customer.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldUpdateAddress() {
        // Given
        Customer customer = createTestCustomer();
        String newAddress = "456 Oak Ave";
        LocalDateTime originalUpdatedAt = customer.getUpdatedAt();

        // When
        customer.updateAddress(newAddress);

        // Then
        assertEquals(newAddress, customer.getAddress());
        assertTrue(customer.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    void shouldReturnTrueForIsNewWhenIdIsNull() {
        // Given
        Customer customer = new Customer(
            new CustomerName("John Doe"),
            "12345678",
            new CustomerEmail("john.doe@example.com"),
            new CustomerPhone("+1234567890"),
            "123 Main St"
        );

        // Then
        assertTrue(customer.isNew());
    }

    @Test
    void shouldReturnFalseForIsNewWhenIdIsPresent() {
        // Given
        Customer customer = createTestCustomer();

        // Then
        assertFalse(customer.isNew());
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        // Given
        CustomerId id = new CustomerId(1L);
        Customer customer1 = new Customer(
            id,
            new CustomerName("John Doe"),
            "12345678",
            new CustomerEmail("john.doe@example.com"),
            new CustomerPhone("+1234567890"),
            "123 Main St",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        Customer customer2 = new Customer(
            id,
            new CustomerName("Jane Smith"),
            "87654321",
            new CustomerEmail("jane.smith@example.com"),
            new CustomerPhone("+0987654321"),
            "456 Oak Ave",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // Then
        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        // Given
        Customer customer1 = new Customer(
            new CustomerId(1L),
            new CustomerName("John Doe"),
            "12345678",
            new CustomerEmail("john.doe@example.com"),
            new CustomerPhone("+1234567890"),
            "123 Main St",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        Customer customer2 = new Customer(
            new CustomerId(2L),
            new CustomerName("John Doe"),
            "12345678",
            new CustomerEmail("john.doe@example.com"),
            new CustomerPhone("+1234567890"),
            "123 Main St",
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        // Then
        assertNotEquals(customer1, customer2);
    }

    @Test
    void shouldHaveValidToString() {
        // Given
        Customer customer = createTestCustomer();

        // When
        String toString = customer.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Customer{"));
        assertTrue(toString.contains("id="));
        assertTrue(toString.contains("fullName="));
        assertTrue(toString.contains("email="));
    }

    private Customer createTestCustomer() {
        return new Customer(
            new CustomerId(1L),
            new CustomerName("John Doe"),
            "12345678",
            new CustomerEmail("john.doe@example.com"),
            new CustomerPhone("+1234567890"),
            "123 Main St",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1)
        );
    }
}
