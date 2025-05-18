package com.sigrap.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating and updating customers.
 * Contains validated customer data for input operations.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates customer input data</li>
 *   <li>Supports create operations</li>
 *   <li>Supports update operations</li>
 *   <li>Manages customer information</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Required fields must not be blank</li>
 *   <li>Email must be valid format</li>
 *   <li>Text fields have maximum length constraints</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for creating or updating a customer")
public class CustomerData {

  /**
   * Full name of the customer.
   */
  @NotBlank(message = "Full name is required")
  @Size(max = 200, message = "Full name must be less than 200 characters")
  @Schema(description = "Customer's full name", example = "John Doe")
  private String fullName;

  /**
   * Document ID (identification number) of the customer.
   */
  @Size(max = 20, message = "Document ID must be less than 20 characters")
  @Schema(
    description = "Customer's identification document number",
    example = "1234567890"
  )
  private String documentId;

  /**
   * Email address of the customer.
   */
  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 255, message = "Email must be less than 255 characters")
  @Schema(
    description = "Customer's email address",
    example = "john.doe@example.com"
  )
  private String email;

  /**
   * Phone number of the customer.
   */
  @Size(max = 20, message = "Phone number must be less than 20 characters")
  @Schema(description = "Customer's phone number", example = "310-555-1234")
  private String phoneNumber;

  /**
   * Physical address of the customer.
   */
  @Size(max = 500, message = "Address must be less than 500 characters")
  @Schema(
    description = "Customer's physical address",
    example = "123 Main St, Anytown"
  )
  private String address;
}
