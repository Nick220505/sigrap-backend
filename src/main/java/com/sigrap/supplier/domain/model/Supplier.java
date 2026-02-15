package com.sigrap.supplier.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a supplier.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 *
 * <p>Suppliers are companies or individuals who provide products to the business.
 * This entity maintains supplier information including contact details, payment terms,
 * and delivery information.</p>
 */
public class Supplier {
    private final SupplierId id;
    private SupplierName name;
    private String contactName;
    private SupplierEmail email;
    private SupplierPhone phone;
    private String address;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a new supplier (without ID).
     * Used when creating a supplier before persistence.
     *
     * @param name the supplier's name (required)
     * @param contactName the contact person's name (optional)
     * @param email the supplier's email address (required)
     * @param phone the supplier's phone number (optional)
     * @param address the supplier's physical address (optional)
     */
    public Supplier(SupplierName name, String contactName, SupplierEmail email,
                   SupplierPhone phone, String address) {
        this(null, name, contactName, email, phone, address,
             LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Full constructor for reconstituting a supplier from persistence.
     *
     * @param id the supplier identifier
     * @param name the supplier's name (required)
     * @param contactName the contact person's name (optional)
     * @param email the supplier's email address (required)
     * @param phone the supplier's phone number (optional)
     * @param address the supplier's physical address (optional)
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public Supplier(SupplierId id, SupplierName name, String contactName,
                   SupplierEmail email, SupplierPhone phone, String address,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Supplier name cannot be null");
        this.contactName = contactName;
        this.email = Objects.requireNonNull(email, "Supplier email cannot be null");
        this.phone = phone;
        this.address = address;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }

    /**
     * Updates the supplier's name.
     *
     * @param newName the new supplier name
     */
    public void updateName(SupplierName newName) {
        Objects.requireNonNull(newName, "New name cannot be null");
        if (!this.name.equals(newName)) {
            this.name = newName;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the contact person's name.
     *
     * @param newContactName the new contact name (can be null)
     */
    public void updateContactName(String newContactName) {
        if (!Objects.equals(this.contactName, newContactName)) {
            this.contactName = newContactName;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the supplier's email address.
     *
     * @param newEmail the new email address
     */
    public void updateEmail(SupplierEmail newEmail) {
        Objects.requireNonNull(newEmail, "New email cannot be null");
        if (!this.email.equals(newEmail)) {
            this.email = newEmail;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the supplier's phone number.
     *
     * @param newPhone the new phone number (can be null)
     */
    public void updatePhone(SupplierPhone newPhone) {
        if (!Objects.equals(this.phone, newPhone)) {
            this.phone = newPhone;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the supplier's address.
     *
     * @param newAddress the new address (can be null)
     */
    public void updateAddress(String newAddress) {
        if (!Objects.equals(this.address, newAddress)) {
            this.address = newAddress;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Checks if this supplier is new (not yet persisted).
     *
     * @return true if the supplier has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    // Getters (no setters - controlled mutation through business methods)

    public SupplierId getId() {
        return id;
    }

    public SupplierName getName() {
        return name;
    }

    public String getContactName() {
        return contactName;
    }

    public SupplierEmail getEmail() {
        return email;
    }

    public SupplierPhone getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return Objects.equals(id, supplier.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", name=" + name +
                ", contactName='" + contactName + '\'' +
                ", email=" + email +
                ", phone=" + phone +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
