package com.sigrap.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating and updating suppliers.
 * Contains validated supplier data for input operations.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates supplier input data</li>
 *   <li>Supports create operations</li>
 *   <li>Supports update operations</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Name must not be blank</li>
 *   <li>Phone numbers must match the pattern</li>
 *   <li>Email must be valid format</li>
 *   <li>Size limitations on text fields</li>
 * </ul></p>
 *
 * <p>Usage Example:
 * <pre>
 * SupplierData supplier = SupplierData.builder()
 *     .name("Office Depot")
 *     .phone("123-456-7890")
 *     .email("contact@officedepot.com")
 *     .build();
 * </pre></p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Supplier creation and update data")
public class SupplierData {

  /**
   * Name of the supplier.
   * Required field, must not be blank.
   */
  @Schema(description = "Name of the supplier", required = true)
  @NotBlank(message = "Supplier name is required")
  @Size(max = 100, message = "Supplier name must be less than 100 characters")
  private String name;

  /**
   * Tax identification number or business ID.
   * Used for billing and legal documentation.
   */
  @Schema(description = "Tax ID of the supplier")
  @Size(max = 20, message = "Tax ID must be less than 20 characters")
  private String taxId;

  /**
   * Name of the person to contact at the supplier.
   */
  @Schema(description = "Contact person at the supplier")
  @Size(max = 100, message = "Contact person must be less than 100 characters")
  private String contactPerson;

  /**
   * Primary phone number for the supplier.
   */
  @Schema(description = "Phone number of the supplier")
  @Pattern(
    regexp = "^[0-9+()\\-\\s]*$",
    message = "Phone number contains invalid characters"
  )
  @Size(max = 20, message = "Phone number must be less than 20 characters")
  private String phone;

  /**
   * Alternative phone number for the supplier.
   */
  @Schema(description = "Alternative phone number")
  @Pattern(
    regexp = "^[0-9+()\\-\\s]*$",
    message = "Alternative phone number contains invalid characters"
  )
  @Size(max = 20, message = "Alternative phone must be less than 20 characters")
  private String alternativePhone;

  /**
   * Email address for contacting the supplier.
   */
  @Schema(description = "Email address of the supplier")
  @Email(message = "Email should be valid")
  @Size(max = 100, message = "Email must be less than 100 characters")
  private String email;

  /**
   * Supplier's physical address.
   */
  @Schema(description = "Physical address of the supplier")
  @Size(max = 255, message = "Address must be less than 255 characters")
  private String address;

  /**
   * Supplier's website URL.
   */
  @Schema(description = "Website URL of the supplier")
  @Size(max = 255, message = "Website must be less than 255 characters")
  private String website;

  /**
   * Description of products or services this supplier provides.
   */
  @Schema(description = "Products or services provided by this supplier")
  @Size(
    max = 500,
    message = "Products provided must be less than 500 characters"
  )
  private String productsProvided;

  /**
   * Average delivery time in days.
   */
  @Schema(description = "Average delivery time in days")
  private Integer averageDeliveryTime;

  /**
   * Payment method accepted by the supplier.
   */
  @Schema(description = "Payment method accepted by the supplier")
  private PaymentMethod paymentMethod;

  /**
   * Payment terms agreed with the supplier.
   */
  @Schema(description = "Payment terms (e.g., 'Net 30', 'Cash on Delivery')")
  @Size(max = 100, message = "Payment terms must be less than 100 characters")
  private String paymentTerms;

  /**
   * Additional notes about the supplier.
   */
  @Schema(description = "Additional notes about the supplier")
  @Size(max = 1000, message = "Notes must be less than 1000 characters")
  private String notes;

  /**
   * Current status of the supplier relationship.
   */
  @Schema(description = "Status of the supplier relationship", required = true)
  private SupplierStatus status;

  /**
   * Supplier rating on a scale of 1-5.
   */
  @Schema(description = "Supplier rating (1-5 scale)")
  private Integer rating;

  /**
   * Whether this is a preferred supplier.
   */
  @Schema(description = "Whether this is a preferred supplier")
  private Boolean preferred;
}
