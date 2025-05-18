package com.sigrap.customer;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object containing customer information.
 * Used for returning customer data in API responses.
 *
 * <p>This class:
 * <ul>
 *   <li>Represents customer read operations</li>
 *   <li>Includes complete customer details</li>
 *   <li>Contains audit timestamps</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing customer information")
public class CustomerInfo {

  /**
   * Unique identifier of the customer.
   */
  @Schema(description = "Unique identifier of the customer", example = "1")
  private Long id;

  /**
   * Full name of the customer.
   */
  @Schema(description = "Customer's full name", example = "John Doe")
  private String fullName;

  /**
   * Document ID (identification number) of the customer.
   */
  @Schema(
    description = "Customer's identification document number",
    example = "1234567890"
  )
  private String documentId;

  /**
   * Email address of the customer.
   */
  @Schema(
    description = "Customer's email address",
    example = "john.doe@example.com"
  )
  private String email;

  /**
   * Phone number of the customer.
   */
  @Schema(description = "Customer's phone number", example = "310-555-1234")
  private String phoneNumber;

  /**
   * Physical address of the customer.
   */
  @Schema(
    description = "Customer's physical address",
    example = "123 Main St, Anytown"
  )
  private String address;

  /**
   * Timestamp when the customer record was created.
   */
  @Schema(
    description = "Date and time when the customer was created",
    example = "2023-01-01T10:00:00"
  )
  private LocalDateTime createdAt;

  /**
   * Timestamp when the customer record was last updated.
   */
  @Schema(
    description = "Date and time when the customer was last updated",
    example = "2023-01-10T15:30:00"
  )
  private LocalDateTime updatedAt;
}
