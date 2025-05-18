package com.sigrap.customer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Customer entity.
 * Provides database operations for customers.
 *
 * <p>This repository includes:
 * <ul>
 *   <li>Basic CRUD operations</li>
 *   <li>Custom search methods</li>
 *   <li>Email-based lookup</li>
 * </ul></p>
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
  /**
   * Finds a customer by their email address.
   *
   * @param email The email address to search for
   * @return Optional containing the customer if found, empty otherwise
   */
  Optional<Customer> findByEmail(String email);

  /**
   * Checks if a customer with the given email exists.
   *
   * @param email The email address to check
   * @return True if a customer with the email exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Finds customers whose full name contains the search term (case insensitive).
   *
   * @param searchTerm The search term to match against the full name
   * @return List of matching customers
   */
  List<Customer> findByFullNameContainingIgnoreCase(String searchTerm);

  /**
   * Finds customers created between the specified dates.
   *
   * @param startDate The start date (inclusive)
   * @param endDate The end date (inclusive)
   * @return List of customers created within the date range
   */
  List<Customer> findByCreatedAtBetween(
    LocalDateTime startDate,
    LocalDateTime endDate
  );
}
