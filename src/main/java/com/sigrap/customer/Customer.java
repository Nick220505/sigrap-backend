package com.sigrap.customer;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a customer in the system.
 * Customers are the individuals who purchase products from the stationery store.
 *
 * <p>This entity maintains customer information including:
 * <ul>
 *   <li>Personal details (name, contact information)</li>
 *   <li>Address information</li>
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
   * Full name of the customer.
   */
  @Column(name = "full_name", nullable = false)
  private String fullName;

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
