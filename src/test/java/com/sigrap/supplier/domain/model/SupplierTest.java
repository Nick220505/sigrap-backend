package com.sigrap.supplier.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Supplier domain entity.
 */
class SupplierTest {

    @Test
    void shouldCreateNewSupplierWithRequiredFields() {
        SupplierName name = new SupplierName("ABC Supplies");
        SupplierEmail email = new SupplierEmail("contact@abc.com");

        Supplier supplier = new Supplier(name, null, email, null, null);

        assertNull(supplier.getId());
        assertEquals(name, supplier.getName());
        assertNull(supplier.getContactName());
        assertEquals(email, supplier.getEmail());
        assertNull(supplier.getPhone());
        assertNull(supplier.getAddress());
        assertNotNull(supplier.getCreatedAt());
        assertNotNull(supplier.getUpdatedAt());
        assertTrue(supplier.isNew());
    }

    @Test
    void shouldCreateSupplierWithAllFields() {
        SupplierId id = new SupplierId(1L);
        SupplierName name = new SupplierName("ABC Supplies");
        String contactName = "John Doe";
        SupplierEmail email = new SupplierEmail("contact@abc.com");
        SupplierPhone phone = new SupplierPhone("123-456-7890");
        String address = "123 Main St";
        LocalDateTime now = LocalDateTime.now();

        Supplier supplier = new Supplier(id, name, contactName, email, phone, address, now, now);

        assertEquals(id, supplier.getId());
        assertEquals(name, supplier.getName());
        assertEquals(contactName, supplier.getContactName());
        assertEquals(email, supplier.getEmail());
        assertEquals(phone, supplier.getPhone());
        assertEquals(address, supplier.getAddress());
        assertEquals(now, supplier.getCreatedAt());
        assertEquals(now, supplier.getUpdatedAt());
        assertFalse(supplier.isNew());
    }

    @Test
    void shouldThrowExceptionForNullName() {
        SupplierEmail email = new SupplierEmail("contact@abc.com");

        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Supplier(null, null, email, null, null)
        );
        assertEquals("Supplier name cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullEmail() {
        SupplierName name = new SupplierName("ABC Supplies");

        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> new Supplier(name, null, null, null, null)
        );
        assertEquals("Supplier email cannot be null", exception.getMessage());
    }

    @Test
    void shouldUpdateName() {
        Supplier supplier = createTestSupplier();
        SupplierName newName = new SupplierName("XYZ Supplies");
        LocalDateTime oldUpdatedAt = supplier.getUpdatedAt();

        // Small delay to ensure timestamp changes
        try { Thread.sleep(10); } catch (InterruptedException e) {}

        supplier.updateName(newName);

        assertEquals(newName, supplier.getName());
        assertTrue(supplier.getUpdatedAt().isAfter(oldUpdatedAt));
    }

    @Test
    void shouldNotUpdateTimestampWhenNameIsSame() {
        Supplier supplier = createTestSupplier();
        SupplierName sameName = new SupplierName("ABC Supplies");
        LocalDateTime oldUpdatedAt = supplier.getUpdatedAt();

        supplier.updateName(sameName);

        assertEquals(sameName, supplier.getName());
        assertEquals(oldUpdatedAt, supplier.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToNullName() {
        Supplier supplier = createTestSupplier();

        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> supplier.updateName(null)
        );
        assertEquals("New name cannot be null", exception.getMessage());
    }

    @Test
    void shouldUpdateContactName() {
        Supplier supplier = createTestSupplier();
        String newContactName = "Jane Smith";
        LocalDateTime oldUpdatedAt = supplier.getUpdatedAt();

        try { Thread.sleep(10); } catch (InterruptedException e) {}

        supplier.updateContactName(newContactName);

        assertEquals(newContactName, supplier.getContactName());
        assertTrue(supplier.getUpdatedAt().isAfter(oldUpdatedAt));
    }

    @Test
    void shouldNotUpdateTimestampWhenContactNameIsSame() {
        Supplier supplier = createTestSupplier();
        LocalDateTime oldUpdatedAt = supplier.getUpdatedAt();

        supplier.updateContactName(null);

        assertNull(supplier.getContactName());
        assertEquals(oldUpdatedAt, supplier.getUpdatedAt());
    }

    @Test
    void shouldUpdateEmail() {
        Supplier supplier = createTestSupplier();
        SupplierEmail newEmail = new SupplierEmail("newemail@abc.com");
        LocalDateTime oldUpdatedAt = supplier.getUpdatedAt();

        try { Thread.sleep(10); } catch (InterruptedException e) {}

        supplier.updateEmail(newEmail);

        assertEquals(newEmail, supplier.getEmail());
        assertTrue(supplier.getUpdatedAt().isAfter(oldUpdatedAt));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToNullEmail() {
        Supplier supplier = createTestSupplier();

        NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> supplier.updateEmail(null)
        );
        assertEquals("New email cannot be null", exception.getMessage());
    }

    @Test
    void shouldUpdatePhone() {
        Supplier supplier = createTestSupplier();
        SupplierPhone newPhone = new SupplierPhone("098-765-4321");
        LocalDateTime oldUpdatedAt = supplier.getUpdatedAt();

        try { Thread.sleep(10); } catch (InterruptedException e) {}

        supplier.updatePhone(newPhone);

        assertEquals(newPhone, supplier.getPhone());
        assertTrue(supplier.getUpdatedAt().isAfter(oldUpdatedAt));
    }

    @Test
    void shouldUpdateAddress() {
        Supplier supplier = createTestSupplier();
        String newAddress = "456 Oak Ave";
        LocalDateTime oldUpdatedAt = supplier.getUpdatedAt();

        try { Thread.sleep(10); } catch (InterruptedException e) {}

        supplier.updateAddress(newAddress);

        assertEquals(newAddress, supplier.getAddress());
        assertTrue(supplier.getUpdatedAt().isAfter(oldUpdatedAt));
    }

    @Test
    void shouldBeEqualForSameId() {
        SupplierId id = new SupplierId(1L);
        Supplier supplier1 = new Supplier(
            id,
            new SupplierName("ABC"),
            null,
            new SupplierEmail("a@b.com"),
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        Supplier supplier2 = new Supplier(
            id,
            new SupplierName("XYZ"),
            null,
            new SupplierEmail("x@y.com"),
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        assertEquals(supplier1, supplier2);
        assertEquals(supplier1.hashCode(), supplier2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentIds() {
        Supplier supplier1 = new Supplier(
            new SupplierId(1L),
            new SupplierName("ABC"),
            null,
            new SupplierEmail("a@b.com"),
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        Supplier supplier2 = new Supplier(
            new SupplierId(2L),
            new SupplierName("ABC"),
            null,
            new SupplierEmail("a@b.com"),
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        assertNotEquals(supplier1, supplier2);
    }

    @Test
    void shouldHaveToStringRepresentation() {
        Supplier supplier = createTestSupplier();
        String toString = supplier.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Supplier"));
        assertTrue(toString.contains("ABC Supplies"));
    }

    private Supplier createTestSupplier() {
        return new Supplier(
            new SupplierName("ABC Supplies"),
            null,
            new SupplierEmail("contact@abc.com"),
            null,
            null
        );
    }
}
