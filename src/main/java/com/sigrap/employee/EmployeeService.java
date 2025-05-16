package com.sigrap.employee;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for employee management operations.
 * Handles business logic for employee-related functionality.
 *
 * <p>This service provides:
 * <ul>
 *   <li>Employee CRUD operations</li>
 *   <li>Status management</li>
 *   <li>Department operations</li>
 *   <li>Employee search functionality</li>
 * </ul></p>
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;

  /**
   * Retrieves all employees.
   *
   * @return List of EmployeeInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<EmployeeInfo> findAll() {
    List<Employee> employees = employeeRepository.findAll();
    return employeeMapper.toInfoList(employees);
  }

  /**
   * Retrieves an employee by their ID.
   *
   * @param id The ID of the employee to retrieve
   * @return EmployeeInfo containing the employee's information
   * @throws EntityNotFoundException if the employee is not found
   */
  @Transactional(readOnly = true)
  public EmployeeInfo findById(Long id) {
    Employee employee = employeeRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Employee not found: " + id)
      );
    return employeeMapper.toInfo(employee);
  }

  /**
   * Creates a new employee.
   *
   * @param data The data for the new employee
   * @return EmployeeInfo containing the created employee's information
   * @throws IllegalArgumentException if an employee already exists with the document ID or email
   */
  @Transactional
  public EmployeeInfo create(EmployeeData data) {
    validateEmployeeData(data);

    if (employeeRepository.existsByDocumentId(data.getDocumentId())) {
      throw new IllegalArgumentException(
        "Employee with this document ID already exists"
      );
    }

    if (employeeRepository.existsByEmail(data.getEmail())) {
      throw new IllegalArgumentException(
        "Employee with this email already exists"
      );
    }

    Employee employee = employeeMapper.toEntity(data);
    employee = employeeRepository.save(employee);
    return employeeMapper.toInfo(employee);
  }

  /**
   * Updates an existing employee.
   *
   * @param id The ID of the employee to update
   * @param data The new data for the employee
   * @return EmployeeInfo containing the updated employee's information
   * @throws EntityNotFoundException if the employee is not found
   * @throws IllegalArgumentException if the document ID or email is already in use
   */
  @Transactional
  public EmployeeInfo update(Long id, EmployeeData data) {
    validateEmployeeData(data);

    Employee employee = employeeRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Employee not found: " + id)
      );

    if (
      !data.getDocumentId().equals(employee.getDocumentId()) &&
      employeeRepository.existsByDocumentId(data.getDocumentId())
    ) {
      throw new IllegalArgumentException(
        "Employee with this document ID already exists"
      );
    }

    if (
      !data.getEmail().equals(employee.getEmail()) &&
      employeeRepository.existsByEmail(data.getEmail())
    ) {
      throw new IllegalArgumentException(
        "Employee with this email already exists"
      );
    }

    employeeMapper.updateEntityFromData(employee, data);
    employee = employeeRepository.save(employee);
    return employeeMapper.toInfo(employee);
  }

  /**
   * Deletes an employee.
   *
   * @param id The ID of the employee to delete
   * @throws EntityNotFoundException if the employee is not found
   */
  @Transactional
  public void delete(Long id) {
    Employee employee = employeeRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Employee not found: " + id)
      );
    employeeRepository.delete(employee);
  }

  /**
   * Deletes multiple employees by their IDs.
   * Validates all IDs exist before performing the deletion.
   *
   * @param ids List of employee IDs to delete
   * @throws EntityNotFoundException if any of the employees is not found
   */
  @Transactional
  public void deleteAllById(List<Long> ids) {
    ids.forEach(id -> {
      if (!employeeRepository.existsById(id)) {
        throw new EntityNotFoundException(
          "Employee with id " + id + " not found"
        );
      }
    });
    employeeRepository.deleteAllByIdInBatch(ids);
  }

  /**
   * Finds an employee by their document ID.
   *
   * @param documentId Document ID to search for
   * @return EmployeeInfo if found
   * @throws EntityNotFoundException if no employee is found with the document ID
   */
  public EmployeeInfo findByDocumentId(String documentId) {
    return employeeRepository
      .findByDocumentId(documentId)
      .map(employeeMapper::toInfo)
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Employee with document ID " + documentId + " not found"
        )
      );
  }

  /**
   * Validates the employee data.
   *
   * @param data Employee data to validate
   */
  private void validateEmployeeData(EmployeeData data) {
    if (data.getUserId() == null) {
      throw new IllegalArgumentException("User ID is required");
    }
    if (data.getFirstName() == null || data.getFirstName().trim().isEmpty()) {
      throw new IllegalArgumentException("First name is required");
    }
    if (data.getLastName() == null || data.getLastName().trim().isEmpty()) {
      throw new IllegalArgumentException("Last name is required");
    }
    if (data.getDocumentId() == null || data.getDocumentId().trim().isEmpty()) {
      throw new IllegalArgumentException("Document ID is required");
    }
    if (data.getEmail() == null || data.getEmail().trim().isEmpty()) {
      throw new IllegalArgumentException("Email is required");
    }
  }
}
