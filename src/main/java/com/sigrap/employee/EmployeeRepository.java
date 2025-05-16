package com.sigrap.employee;

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
   * Finds an employee by their email.
   *
   * @param email Email of the employee
   * @return Optional containing the employee if found, otherwise empty
   */
  Optional<Employee> findByEmail(String email);

  /**
   * Finds an employee by their document ID.
   *
   * @param documentId Document ID of the employee
   * @return Optional containing the employee if found, otherwise empty
   */
  Optional<Employee> findByDocumentId(String documentId);

  /**
   * Checks if an employee exists with the given document ID.
   *
   * @param documentId Document ID to check
   * @return True if an employee with this document ID exists, false otherwise
   */
  boolean existsByDocumentId(String documentId);

  /**
   * Checks if an employee exists with the given email.
   *
   * @param email Email to check
   * @return True if an employee with this email exists, false otherwise
   */
  boolean existsByEmail(String email);
}
