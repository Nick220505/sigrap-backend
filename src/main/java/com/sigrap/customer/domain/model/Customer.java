package com.sigrap.customer.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a customer.
 * This is a pure POJO with no framework dependencies, containing business logic and invariants.
 *
 * <p>Customers are individuals who purchase products from the stationery store.
 * This entity maintains customer information including personal details, contact information,
 * and address information.</p>
 */
public class Customer {
    private final CustomerId id;
    private CustomerName fullName;
    private String documentId;
    private CustomerEmail email;
    private CustomerPhone phoneNumber;
    private String address;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructor for creating a new customer (without ID).
     * Used when creating a customer before persistence.
     *
     * @param fullName the customer's full name (required)
     * @param documentId the customer's document ID (optional)
     * @param email the customer's email address (required)
     * @param phoneNumber the customer's phone number (optional)
     * @param address the customer's physical address (optional)
     */
    public Customer(CustomerName fullName, String documentId, CustomerEmail email,
                   CustomerPhone phoneNumber, String address) {
        this(null, fullName, documentId, email, phoneNumber, address,
             LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * Full constructor for reconstituting a customer from persistence.
     *
     * @param id the customer identifier
     * @param fullName the customer's full name (required)
     * @param documentId the customer's document ID (optional)
     * @param email the customer's email address (required)
     * @param phoneNumber the customer's phone number (optional)
     * @param address the customer's physical address (optional)
     * @param createdAt the creation timestamp
     * @param updatedAt the last update timestamp
     */
    public Customer(CustomerId id, CustomerName fullName, String documentId,
                   CustomerEmail email, CustomerPhone phoneNumber, String address,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = Objects.requireNonNull(fullName, "Customer name cannot be null");
        this.documentId = documentId;
        this.email = Objects.requireNonNull(email, "Customer email cannot be null");
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }

    /**
     * Updates the customer's full name.
     *
     * @param newFullName the new full name
     */
    public void updateFullName(CustomerName newFullName) {
        Objects.requireNonNull(newFullName, "New full name cannot be null");
        if (!this.fullName.equals(newFullName)) {
            this.fullName = newFullName;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the customer's document ID.
     *
     * @param newDocumentId the new document ID (can be null)
     */
    public void updateDocumentId(String newDocumentId) {
        if (!Objects.equals(this.documentId, newDocumentId)) {
            this.documentId = newDocumentId;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the customer's email address.
     *
     * @param newEmail the new email address
     */
    public void updateEmail(CustomerEmail newEmail) {
        Objects.requireNonNull(newEmail, "New email cannot be null");
        if (!this.email.equals(newEmail)) {
            this.email = newEmail;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the customer's phone number.
     *
     * @param newPhoneNumber the new phone number (can be null)
     */
    public void updatePhoneNumber(CustomerPhone newPhoneNumber) {
        if (!Objects.equals(this.phoneNumber, newPhoneNumber)) {
            this.phoneNumber = newPhoneNumber;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Updates the customer's address.
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
     * Checks if this customer is new (not yet persisted).
     *
     * @return true if the customer has no ID, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }

    // Getters (no setters - controlled mutation through business methods)

    public CustomerId getId() {
        return id;
    }

    public CustomerName getFullName() {
        return fullName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public CustomerEmail getEmail() {
        return email;
    }

    public CustomerPhone getPhoneNumber() {
        return phoneNumber;
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
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", fullName=" + fullName +
                ", documentId='" + documentId + '\'' +
                ", email=" + email +
                ", phoneNumber=" + phoneNumber +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
