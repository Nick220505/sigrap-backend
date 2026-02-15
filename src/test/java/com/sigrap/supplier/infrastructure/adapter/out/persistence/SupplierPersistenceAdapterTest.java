package com.sigrap.supplier.infrastructure.adapter.out.persistence;

import com.sigrap.supplier.domain.model.Supplier;
import com.sigrap.supplier.domain.model.SupplierId;
import com.sigrap.supplier.domain.model.SupplierEmail;
import com.sigrap.supplier.domain.model.SupplierName;
import com.sigrap.supplier.domain.model.SupplierPhone;
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
 * Integration tests for SupplierPersistenceAdapter.
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
class SupplierPersistenceAdapterTest {

    @Autowired
    private SupplierPersistenceAdapter adapter;

    @Autowired
    private SupplierJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveNewSupplier() {
        // Given
        Supplier supplier = new Supplier(
            new SupplierName("ABC Supplies"),
            "John Smith",
            new SupplierEmail("john@abcsupplies.com"),
            new SupplierPhone("555-1234"),
            "123 Main St"
        );

        // When
        Supplier saved = adapter.save(supplier);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("ABC Supplies", saved.getName().value());
        assertEquals("John Smith", saved.getContactName());
        assertEquals("john@abcsupplies.com", saved.getEmail().value());
        assertEquals("555-1234", saved.getPhone().value());
        assertEquals("123 Main St", saved.getAddress());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldUpdateExistingSupplier() {
        // Given - save initial supplier
        Supplier supplier = new Supplier(
            new SupplierName("XYZ Corp"),
            "Jane Doe",
            new SupplierEmail("jane@xyzcorp.com"),
            new SupplierPhone("555-5678"),
            "456 Oak Ave"
        );
        Supplier saved = adapter.save(supplier);
        
        // When - update the supplier
        Supplier updated = new Supplier(
            saved.getId(),
            new SupplierName("XYZ Corporation"),
            "Jane Doe-Smith",
            new SupplierEmail("jane.smith@xyzcorp.com"),
            new SupplierPhone("555-9999"),
            "789 Pine Rd",
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );
        Supplier result = adapter.save(updated);

        // Then
        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("XYZ Corporation", result.getName().value());
        assertEquals("Jane Doe-Smith", result.getContactName());
        assertEquals("jane.smith@xyzcorp.com", result.getEmail().value());
        assertEquals("555-9999", result.getPhone().value());
        assertEquals("789 Pine Rd", result.getAddress());
    }

    @Test
    void shouldFindSupplierById() {
        // Given
        Supplier supplier = new Supplier(
            new SupplierName("Tech Supplies"),
            "Bob Wilson",
            new SupplierEmail("bob@techsupplies.com"),
            new SupplierPhone("555-1111"),
            "321 Elm St"
        );
        Supplier saved = adapter.save(supplier);

        // When
        Optional<Supplier> found = adapter.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Tech Supplies", found.get().getName().value());
        assertEquals("bob@techsupplies.com", found.get().getEmail().value());
    }

    @Test
    void shouldReturnEmptyWhenSupplierNotFound() {
        // Given
        SupplierId nonExistentId = new SupplierId(999L);

        // When
        Optional<Supplier> found = adapter.findById(nonExistentId);

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindAllSuppliers() {
        // Given
        adapter.save(new Supplier(
            new SupplierName("Supplier 1"),
            "Contact 1",
            new SupplierEmail("supplier1@example.com"),
            new SupplierPhone("555-0001"),
            "Address 1"
        ));
        adapter.save(new Supplier(
            new SupplierName("Supplier 2"),
            "Contact 2",
            new SupplierEmail("supplier2@example.com"),
            new SupplierPhone("555-0002"),
            "Address 2"
        ));
        adapter.save(new Supplier(
            new SupplierName("Supplier 3"),
            "Contact 3",
            new SupplierEmail("supplier3@example.com"),
            null,
            null
        ));

        // When
        List<Supplier> suppliers = adapter.findAll();

        // Then
        assertNotNull(suppliers);
        assertEquals(3, suppliers.size());
        assertTrue(suppliers.stream().anyMatch(s -> s.getName().value().equals("Supplier 1")));
        assertTrue(suppliers.stream().anyMatch(s -> s.getName().value().equals("Supplier 2")));
        assertTrue(suppliers.stream().anyMatch(s -> s.getName().value().equals("Supplier 3")));
    }

    @Test
    void shouldReturnEmptyListWhenNoSuppliers() {
        // When
        List<Supplier> suppliers = adapter.findAll();

        // Then
        assertNotNull(suppliers);
        assertTrue(suppliers.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        // Given
        adapter.save(new Supplier(
            new SupplierName("Unique Supplier"),
            "Alice Brown",
            new SupplierEmail("alice@unique.com"),
            new SupplierPhone("555-2222"),
            "654 Maple Dr"
        ));

        // When
        boolean exists = adapter.existsByEmail(new SupplierEmail("alice@unique.com"));

        // Then
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        // Given
        SupplierEmail nonExistentEmail = new SupplierEmail("nonexistent@example.com");

        // When
        boolean exists = adapter.existsByEmail(nonExistentEmail);

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldReturnTrueWhenEmailExistsForDifferentId() {
        // Given
        Supplier supplier1 = adapter.save(new Supplier(
            new SupplierName("Supplier One"),
            "Contact One",
            new SupplierEmail("duplicate@example.com"),
            new SupplierPhone("555-1111"),
            "Address 1"
        ));
        
        adapter.save(new Supplier(
            new SupplierName("Supplier Two"),
            "Contact Two",
            new SupplierEmail("another@example.com"),
            new SupplierPhone("555-2222"),
            "Address 2"
        ));

        // When
        boolean exists = adapter.existsByEmailAndIdNot(
            new SupplierEmail("another@example.com"),
            supplier1.getId()
        );

        // Then
        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailOnlyExistsForExcludedId() {
        // Given
        Supplier supplier = adapter.save(new Supplier(
            new SupplierName("Unique Supplier"),
            "Unique Contact",
            new SupplierEmail("unique@example.com"),
            new SupplierPhone("555-9999"),
            "Unique Address"
        ));

        // When
        boolean exists = adapter.existsByEmailAndIdNot(
            new SupplierEmail("unique@example.com"),
            supplier.getId()
        );

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldDeleteSupplierById() {
        // Given
        Supplier supplier = new Supplier(
            new SupplierName("Temporary Supplier"),
            "Temp Contact",
            new SupplierEmail("temp@example.com"),
            new SupplierPhone("555-0000"),
            "Temp Address"
        );
        Supplier saved = adapter.save(supplier);
        SupplierId savedId = saved.getId();

        // When
        adapter.deleteById(savedId);

        // Then
        Optional<Supplier> found = adapter.findById(savedId);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldDeleteMultipleSuppliersById() {
        // Given
        Supplier supp1 = adapter.save(new Supplier(
            new SupplierName("Supplier A"),
            "Contact A",
            new SupplierEmail("supplierA@example.com"),
            new SupplierPhone("555-1111"),
            "Address A"
        ));
        Supplier supp2 = adapter.save(new Supplier(
            new SupplierName("Supplier B"),
            "Contact B",
            new SupplierEmail("supplierB@example.com"),
            new SupplierPhone("555-2222"),
            "Address B"
        ));
        Supplier supp3 = adapter.save(new Supplier(
            new SupplierName("Supplier C"),
            "Contact C",
            new SupplierEmail("supplierC@example.com"),
            new SupplierPhone("555-3333"),
            "Address C"
        ));
        
        List<SupplierId> idsToDelete = List.of(supp1.getId(), supp2.getId());

        // When
        adapter.deleteAllById(idsToDelete);

        // Then
        assertTrue(adapter.findById(supp1.getId()).isEmpty());
        assertTrue(adapter.findById(supp2.getId()).isEmpty());
        assertTrue(adapter.findById(supp3.getId()).isPresent());
    }

    @Test
    void shouldPreserveTimestampsWhenSaving() {
        // Given
        Supplier supplier = new Supplier(
            new SupplierName("Test Supplier"),
            "Test Contact",
            new SupplierEmail("test@example.com"),
            new SupplierPhone("555-TEST"),
            "Test Address"
        );

        // When
        Supplier saved = adapter.save(supplier);

        // Then
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void shouldHandleSupplierWithNullOptionalFields() {
        // Given
        Supplier supplier = new Supplier(
            new SupplierName("Minimal Supplier"),
            null,
            new SupplierEmail("minimal@example.com"),
            null,
            null
        );

        // When
        Supplier saved = adapter.save(supplier);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Minimal Supplier", saved.getName().value());
        assertNull(saved.getContactName());
        assertEquals("minimal@example.com", saved.getEmail().value());
        assertNull(saved.getPhone());
        assertNull(saved.getAddress());
    }

    @Test
    void shouldMapAllFieldsCorrectlyBetweenDomainAndJpa() {
        // Given
        Supplier supplier = new Supplier(
            new SupplierName("Complete Supplier"),
            "Complete Contact",
            new SupplierEmail("complete@example.com"),
            new SupplierPhone("555-COMPLETE"),
            "123 Complete Street, City, State 12345"
        );

        // When
        Supplier saved = adapter.save(supplier);
        Optional<Supplier> retrieved = adapter.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        Supplier result = retrieved.get();
        assertEquals(saved.getId().value(), result.getId().value());
        assertEquals(saved.getName().value(), result.getName().value());
        assertEquals(saved.getContactName(), result.getContactName());
        assertEquals(saved.getEmail().value(), result.getEmail().value());
        assertEquals(saved.getPhone().value(), result.getPhone().value());
        assertEquals(saved.getAddress(), result.getAddress());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void shouldEnforceUniqueEmailConstraint() {
        // Given
        adapter.save(new Supplier(
            new SupplierName("First Supplier"),
            "First Contact",
            new SupplierEmail("unique@example.com"),
            new SupplierPhone("555-1111"),
            "First Address"
        ));

        // When & Then
        assertThrows(Exception.class, () -> {
            adapter.save(new Supplier(
                new SupplierName("Second Supplier"),
                "Second Contact",
                new SupplierEmail("unique@example.com"),
                new SupplierPhone("555-2222"),
                "Second Address"
            ));
        });
    }
}
