package com.sigrap.customer;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for customer management operations.
 * Handles business logic for customer-related functionality.
 *
 * <p>This service provides:
 * <ul>
 *   <li>Customer CRUD operations</li>
 *   <li>Status management</li>
 *   <li>Customer search functionality</li>
 *   <li>Data validation</li>
 * </ul></p>
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  /**
   * Retrieves all customers.
   *
   * @return List of customer information DTOs
   */
  @Transactional(readOnly = true)
  public List<CustomerInfo> findAll() {
    return customerMapper.toCustomerInfoList(customerRepository.findAll());
  }

  /**
   * Finds a customer by their ID.
   *
   * @param id The customer ID to search for
   * @return The customer information DTO
   * @throws EntityNotFoundException if customer with the ID is not found
   */
  @Transactional(readOnly = true)
  public CustomerInfo findById(Long id) {
    return customerRepository
      .findById(id)
      .map(customerMapper::toCustomerInfo)
      .orElseThrow(() ->
        new EntityNotFoundException("Customer not found with ID: " + id)
      );
  }

  /**
   * Creates a new customer.
   *
   * @param customerData The customer data for creation
   * @return The created customer information DTO
   * @throws IllegalArgumentException if a customer with the same email already exists
   */
  @Transactional
  public CustomerInfo create(CustomerData customerData) {
    if (customerRepository.existsByEmail(customerData.getEmail())) {
      throw new IllegalArgumentException(
        "Customer with email already exists: " + customerData.getEmail()
      );
    }

    Customer customer = customerMapper.toCustomer(customerData);
    Customer savedCustomer = customerRepository.save(customer);
    return customerMapper.toCustomerInfo(savedCustomer);
  }

  /**
   * Updates an existing customer.
   *
   * @param id The ID of the customer to update
   * @param customerData The new customer data
   * @return The updated customer information DTO
   * @throws EntityNotFoundException if customer with the ID is not found
   * @throws IllegalArgumentException if trying to update to an email that's already taken by another customer
   */
  @Transactional
  public CustomerInfo update(Long id, CustomerData customerData) {
    Customer customer = customerRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Customer not found with ID: " + id)
      );

    // Check if email is being changed and is already taken by another customer
    if (
      !customer.getEmail().equals(customerData.getEmail()) &&
      customerRepository.existsByEmail(customerData.getEmail())
    ) {
      throw new IllegalArgumentException(
        "Email already in use by another customer: " + customerData.getEmail()
      );
    }

    customerMapper.updateCustomerFromDto(customer, customerData);
    Customer updatedCustomer = customerRepository.save(customer);
    return customerMapper.toCustomerInfo(updatedCustomer);
  }

  /**
   * Deletes a customer by ID.
   *
   * @param id The ID of the customer to delete
   * @throws EntityNotFoundException if customer with the ID is not found
   */
  @Transactional
  public void delete(Long id) {
    if (!customerRepository.existsById(id)) {
      throw new EntityNotFoundException("Customer not found with ID: " + id);
    }
    customerRepository.deleteById(id);
  }

  /**
   * Changes the status of a customer.
   *
   * @param id The ID of the customer
   * @param status The new status to set
   * @return The updated customer information DTO
   * @throws EntityNotFoundException if customer with the ID is not found
   */
  @Transactional
  public CustomerInfo updateStatus(Long id, CustomerStatus status) {
    Customer customer = customerRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Customer not found with ID: " + id)
      );

    customer.setStatus(status);
    Customer updatedCustomer = customerRepository.save(customer);
    return customerMapper.toCustomerInfo(updatedCustomer);
  }

  /**
   * Finds customers by their status.
   *
   * @param status The status to filter by
   * @return List of customer information DTOs with the specified status
   */
  @Transactional(readOnly = true)
  public List<CustomerInfo> findByStatus(CustomerStatus status) {
    return customerMapper.toCustomerInfoList(
      customerRepository.findByStatus(status)
    );
  }

  /**
   * Searches for customers by name.
   *
   * @param searchTerm The search term to match against names
   * @return List of matching customer information DTOs
   */
  @Transactional(readOnly = true)
  public List<CustomerInfo> searchByName(String searchTerm) {
    return customerMapper.toCustomerInfoList(
      customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        searchTerm,
        searchTerm
      )
    );
  }

  /**
   * Finds customers created within a date range.
   *
   * @param startDate The start date (inclusive)
   * @param endDate The end date (inclusive)
   * @return List of customer information DTOs created within the specified date range
   */
  @Transactional(readOnly = true)
  public List<CustomerInfo> findByCreatedDateRange(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    return customerMapper.toCustomerInfoList(
      customerRepository.findByCreatedAtBetween(startDate, endDate)
    );
  }
}
