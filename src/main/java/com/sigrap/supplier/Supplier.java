package com.sigrap.supplier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
   * Tax identification number or business ID.
   * Used for billing and legal documentation.
   */
  @Size(max = 20)
  private String taxId;

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
   * Payment methods accepted by the supplier.
   * Uses enum to restrict possible values.
   */
  @Enumerated(EnumType.STRING)
  private PaymentMethod paymentMethod;

  /**
   * Payment terms agreed with the supplier (e.g., "Net 30", "Cash on Delivery").
   * Maximum length of 100 characters.
   */
  @Size(max = 100)
  private String paymentTerms;

  /**
   * General notes about the supplier relationship, special arrangements, etc.
   * Text field with maximum length of 1000 characters.
   */
  @Size(max = 1000)
  private String notes;

  /**
   * Status of the supplier relationship.
   * Uses enum to restrict possible values.
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SupplierStatus status;

  /**
   * Rating of the supplier (1-5 scale).
   * Used to evaluate supplier performance.
   */
  private Integer rating;

  /**
   * Flag indicating if this is a preferred supplier.
   * Preferred suppliers may get priority in ordering.
   */
  private Boolean preferred;

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
