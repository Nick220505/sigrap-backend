package com.sigrap.employee;

import com.sigrap.user.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object containing employee information.
 * Used for returning employee data in API responses.
 *
 * <p>This class:
 * <ul>
 *   <li>Represents employee read operations</li>
 *   <li>Includes complete employee details</li>
 *   <li>Contains audit timestamps</li>
 *   <li>Provides status information</li>
 * </ul></p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing employee information")
public class EmployeeInfo {

  /**
   * Unique identifier of the employee.
   */
  @Schema(description = "Unique identifier of the employee", example = "1")
  private Long id;

  /**
   * ID of the associated user account.
   */
  @Schema(description = "ID of the associated user account", example = "1")
  private Long userId;

  /**
   * First name of the employee.
   */
  @Schema(description = "First name of the employee", example = "John")
  private String firstName;

  /**
   * Last name of the employee.
   */
  @Schema(description = "Last name of the employee", example = "Doe")
  private String lastName;

  /**
   * Document ID (identification number) of the employee.
   */
  @Schema(description = "Document ID of the employee", example = "123456789")
  private String documentId;

  /**
   * Phone number of the employee.
   */
  @Schema(description = "Phone number of the employee", example = "+1234567890")
  private String phoneNumber;

  /**
   * Email address of the employee.
   */
  @Schema(
    description = "Email address of the employee",
    example = "john.doe@example.com"
  )
  private String email;

  /**
   * Position or job title of the employee.
   */
  @Schema(description = "Position of the employee", example = "Sales Associate")
  private String position;

  /**
   * Department the employee belongs to.
   */
  @Schema(description = "Department of the employee", example = "Sales")
  private String department;

  /**
   * Date when the employee was hired.
   */
  @Schema(
    description = "Hire date of the employee",
    example = "2023-01-15T09:00:00"
  )
  private LocalDateTime hireDate;

  /**
   * Date when the employee was terminated.
   */
  @Schema(
    description = "Termination date of the employee",
    example = "2024-01-15T09:00:00"
  )
  private LocalDateTime terminationDate;

  /**
   * Current status of the employee.
   */
  @Schema(description = "Current status of the employee", example = "ACTIVE")
  private Employee.EmployeeStatus status;

  /**
   * URL of the employee's profile image.
   */
  @Schema(
    description = "URL of the employee's profile image",
    example = "https://example.com/profile.jpg"
  )
  private String profileImageUrl;

  /**
   * Timestamp of when the employee record was created.
   */
  @Schema(
    description = "Creation timestamp of the employee record",
    example = "2023-01-15T09:00:00"
  )
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the employee record was last updated.
   */
  @Schema(
    description = "Last update timestamp of the employee record",
    example = "2023-01-15T09:00:00"
  )
  private LocalDateTime updatedAt;

  /**
   * User information associated with the employee.
   */
  @Schema(description = "User information associated with the employee")
  private UserInfo user;
}
