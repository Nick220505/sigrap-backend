package com.sigrap.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity class representing a customer in the system.
 * Customers are the individuals who purchase products from the stationery store.
 *
 * <p>This entity maintains customer information including:
 * <ul>
 *   <li>Personal details (name, contact information)</li>
 *   <li>Address information</li>
 *   <li>Status tracking</li>
 *   <li>Audit timestamps</li>
 * </ul></p>
 */
@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

  /**
   * The unique identifier for this customer.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * First name of the customer.
   */
  @Column(name = "first_name", nullable = false)
  private String firstName;

  /**
   * Last name of the customer.
   */
  @Column(name = "last_name", nullable = false)
  private String lastName;

  /**
   * Document ID (identification number) of the customer.
   */
  @Column(name = "document_id")
  private String documentId;

  /**
   * Email address of the customer, used for communications.
   */
  @Column(nullable = false, unique = true)
  private String email;

  /**
   * Phone number of the customer.
   */
  @Column(name = "phone_number")
  private String phoneNumber;

  /**
   * Physical address of the customer.
   */
  private String address;

  /**
   * Current status of the customer.
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CustomerStatus status;

  /**
   * Timestamp when the customer record was created.
   */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp when the customer record was last updated.
   */
  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
