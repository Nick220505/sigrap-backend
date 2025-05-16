package com.sigrap.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating and updating employees.
 * Contains validated employee data for input operations.
 *
 * <p>This class:
 * <ul>
 *   <li>Validates employee input data</li>
 *   <li>Supports create operations</li>
 *   <li>Supports update operations</li>
 *   <li>Manages employee information</li>
 * </ul></p>
 *
 * <p>Validation Rules:
 * <ul>
 *   <li>Required fields must not be blank</li>
 *   <li>Email must be valid format</li>
 *   <li>Document ID must be unique</li>
 *   <li>User ID must reference existing user</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
  description = "Data transfer object for creating or updating an employee"
)
public class EmployeeData {

  /**
   * ID of the associated user account.
   * Must reference an existing user.
   */
  @NotNull(message = "User ID cannot be null")
  @Schema(description = "ID of the associated user account", example = "1")
  private Long userId;

  /**
   * First name of the employee.
   * Must not be blank.
   */
  @NotBlank(message = "First name cannot be blank")
  @Schema(description = "First name of the employee", example = "John")
  private String firstName;

  /**
   * Last name of the employee.
   * Must not be blank.
   */
  @NotBlank(message = "Last name cannot be blank")
  @Schema(description = "Last name of the employee", example = "Doe")
  private String lastName;

  /**
   * Document ID (identification number) of the employee.
   * Must not be blank and should be unique.
   */
  @NotBlank(message = "Document ID cannot be blank")
  @Schema(description = "Document ID of the employee", example = "123456789")
  private String documentId;

  /**
   * Phone number of the employee.
   */
  @Schema(description = "Phone number of the employee", example = "+1234567890")
  private String phoneNumber;

  /**
   * Email address of the employee.
   * Must be a valid email format.
   */
  @Email(message = "Invalid email format")
  @Schema(
    description = "Email address of the employee",
    example = "john.doe@example.com"
  )
  private String email;

  /**
   * Date when the employee was hired.
   * Must not be null.
   */
  @NotNull(message = "Hire date cannot be null")
  @Schema(
    description = "Hire date of the employee",
    example = "2023-01-15T09:00:00"
  )
  private LocalDateTime hireDate;

  /**
   * URL of the employee's profile image.
   */
  @Schema(
    description = "URL of the employee's profile image",
    example = "https://example.com/profile.jpg"
  )
  private String profileImageUrl;

  /**
   * Status of the employee.
   */
  @Schema(description = "Status of the employee")
  private EmployeeStatus status;
}
