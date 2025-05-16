package com.sigrap.employee;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Employee entity.
 * Provides database operations for employees.
 *
 * <p>This repository includes:
 * <ul>
 *   <li>Basic CRUD operations</li>
 *   <li>Custom search methods</li>
 *   <li>Status-based queries</li>
 *   <li>Department-based queries</li>
 * </ul></p>
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  /**
   * Finds an employee by their user ID.
   *
   * @param userId ID of the associated user account
   * @return Optional containing the employee if found
   */
  Optional<Employee> findByUserId(Long userId);

  /**
   * Finds all employees hired between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of employees hired within the date range
   */
  List<Employee> findByHireDateBetween(
    LocalDateTime startDate,
    LocalDateTime endDate
  );

  /**
   * Finds an employee by their document ID.
   *
   * @param documentId Document ID to search for
   * @return Optional containing the employee if found
   */
  Optional<Employee> findByDocumentId(String documentId);

  /**
   * Finds an employee by their email address.
   *
   * @param email Email address to search for
   * @return Optional containing the employee if found
   */
  Optional<Employee> findByEmail(String email);

  /**
   * Checks if an employee exists with the given document ID.
   *
   * @param documentId Document ID to check
   * @return true if an employee exists with the document ID
   */
  boolean existsByDocumentId(String documentId);

  /**
   * Checks if an employee exists with the given email.
   *
   * @param email Email address to check
   * @return true if an employee exists with the email
   */
  boolean existsByEmail(String email);
}
