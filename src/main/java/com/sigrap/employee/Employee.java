package com.sigrap.employee;

import com.sigrap.employee.attendance.Attendance;
import com.sigrap.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity class representing an employee in the system.
 * Employees are the staff members who work in the stationery store.
 *
 * <p>This entity maintains employee information including:
 * <ul>
 *   <li>Personal details (name, contact info)</li>
 *   <li>Employment details (position, department)</li>
 *   <li>Status tracking</li>
 *   <li>Attendance records</li>
 *   <li>Performance metrics</li>
 * </ul></p>
 */
@Entity
@Table(name = "employees")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

  /**
   * Unique identifier for the employee.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Associated user account for this employee.
   * One-to-one relationship with User entity.
   */
  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  @NotNull(message = "User reference cannot be null")
  private User user;

  /**
   * First name of the employee.
   * Must not be blank.
   */
  @NotBlank(message = "First name cannot be blank")
  @Column(name = "first_name", nullable = false)
  private String firstName;

  /**
   * Last name of the employee.
   * Must not be blank.
   */
  @NotBlank(message = "Last name cannot be blank")
  @Column(name = "last_name", nullable = false)
  private String lastName;

  /**
   * Document ID (identification number) of the employee.
   * Must not be blank and should be unique.
   */
  @NotBlank(message = "Document ID cannot be blank")
  @Column(name = "document_id", nullable = false, unique = true)
  private String documentId;

  /**
   * Phone number of the employee.
   */
  @Column(name = "phone_number")
  private String phoneNumber;

  /**
   * Email address of the employee.
   * Must be a valid email format.
   */
  @Email(message = "Invalid email format")
  @Column(unique = true)
  private String email;

  /**
   * Date when the employee was terminated.
   * Null if still employed.
   */
  @Column(name = "termination_date")
  private LocalDateTime terminationDate;

  /**
   * URL of the employee's profile image.
   */
  @Column(name = "profile_image_url")
  private String profileImageUrl;

  /**
   * Collection of attendance records for this employee.
   * One-to-many relationship with Attendance.
   */
  @OneToMany(mappedBy = "employee")
  @Builder.Default
  @EqualsAndHashCode.Exclude
  private Set<Attendance> attendanceRecords = new HashSet<>();

  /**
   * Timestamp of when the employee record was created.
   * Automatically set during entity creation.
   */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the employee record was last updated.
   * Automatically updated when the entity is modified.
   */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
