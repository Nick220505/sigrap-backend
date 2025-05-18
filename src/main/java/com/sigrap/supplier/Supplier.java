package com.sigrap.supplier;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a supplier in the system.
 * Suppliers provide products to the business.
 *
 * <p>This entity stores all supplier contact information, payment terms,
 * delivery times, and other important business relationship data.</p>
 */
@Entity
@Table(name = "suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {

  /**
   * Unique identifier for the supplier.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Name of the supplier company or individual.
   * Must not be blank and has a maximum length of 100 characters.
   */
  @NotBlank
  @Size(max = 100)
  @Column(nullable = false)
  private String name;

  /**
   * Contact person's name at the supplier company.
   * Optional field with maximum length of 100 characters.
   */
  @Size(max = 100)
  private String contactPerson;

  /**
   * Phone number for contacting the supplier.
   * Uses a pattern to ensure valid format.
   */
  @Pattern(
    regexp = "^[0-9+()\\-\\s]*$",
    message = "Phone number contains invalid characters"
  )
  @Size(max = 20)
  private String phone;

  /**
   * Alternative phone number for the supplier.
   * Optional field with validation pattern.
   */
  @Pattern(
    regexp = "^[0-9+()\\-\\s]*$",
    message = "Alternative phone number contains invalid characters"
  )
  @Size(max = 20)
  private String alternativePhone;

  /**
   * Email address for the supplier.
   * Validated for proper email format.
   */
  @Email
  @Size(max = 100)
  private String email;

  /**
   * Physical address of the supplier.
   * Maximum length of 255 characters.
   */
  @Size(max = 255)
  private String address;

  /**
   * Website URL of the supplier.
   * Maximum length of 255 characters.
   */
  @Size(max = 255)
  private String website;

  /**
   * Description of products or services provided by this supplier.
   * Text field with maximum length of 500 characters.
   */
  @Size(max = 500)
  private String productsProvided;

  /**
   * Average delivery time in days.
   * Helps in inventory planning and order scheduling.
   */
  private Integer averageDeliveryTime;

  /**
   * Payment terms agreed with the supplier (e.g., "Net 30", "Cash on Delivery").
   * Maximum length of 100 characters.
   */
  @Size(max = 100)
  private String paymentTerms;

  /**
   * Timestamp of when the supplier record was created.
   * Automatically set during entity creation.
   */
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the supplier record was last updated.
   * Automatically updated when the entity is modified.
   */
  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
