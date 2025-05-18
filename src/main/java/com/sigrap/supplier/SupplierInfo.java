package com.sigrap.supplier;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object containing supplier information.
 * Used for returning supplier data in API responses.
 *
 * <p>This class:
 * <ul>
 *   <li>Represents supplier read operations</li>
 *   <li>Includes audit information</li>
 *   <li>Contains complete supplier details</li>
 * </ul></p>
 *
 * <p>Key Features:
 * <ul>
 *   <li>Immutable supplier data</li>
 *   <li>Timestamp tracking</li>
 *   <li>Complete supplier representation</li>
 * </ul></p>
 *
 * <p>Usage Example:
 * <pre>
 * SupplierInfo info = SupplierInfo.builder()
 *     .id(1)
 *     .name("Office Depot")
 *     .email("contact@officedepot.com")
 *     .build();
 * </pre></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Supplier information response")
public class SupplierInfo {

  /**
   * Unique identifier for the supplier.
   */
  @Schema(description = "Unique identifier", example = "1")
  private Long id;

  /**
   * Name of the supplier company or individual.
   */
  @Schema(description = "Supplier name", example = "Office Depot")
  private String name;

  /**
   * Name of the contact person at the supplier.
   */
  @Schema(description = "Contact person", example = "Miguel Sánchez")
  private String contactPerson;

  /**
   * Primary phone number for contacting the supplier.
   */
  @Schema(description = "Phone number", example = "(01) 619-2700")
  private String phone;

  /**
   * Alternative phone number for the supplier.
   */
  @Schema(description = "Alternative phone", example = "(01) 619-2701")
  private String alternativePhone;

  /**
   * Email address for contacting the supplier.
   */
  @Schema(description = "Email address", example = "msanchez@officedepot.com")
  private String email;

  /**
   * Physical address of the supplier's location.
   */
  @Schema(
    description = "Physical address",
    example = "Av. Javier Prado Este 2558, San Borja, Lima"
  )
  private String address;

  /**
   * Website URL of the supplier.
   */
  @Schema(
    description = "Website URL",
    example = "https://www.officedepot.com.pe"
  )
  private String website;

  /**
   * Description of products or services the supplier provides.
   */
  @Schema(
    description = "Products or services provided",
    example = "Artículos de oficina y escolares"
  )
  private String productsProvided;

  /**
   * Average delivery time in days.
   */
  @Schema(description = "Average delivery time (days)", example = "3")
  private Integer averageDeliveryTime;

  /**
   * Payment terms for purchases from this supplier.
   */
  @Schema(description = "Payment terms", example = "30 días")
  private String paymentTerms;

  /**
   * Timestamp when the supplier record was created.
   */
  @Schema(description = "Creation timestamp")
  private LocalDateTime createdAt;

  /**
   * Timestamp when the supplier record was last updated.
   */
  @Schema(description = "Last update timestamp")
  private LocalDateTime updatedAt;
}
