package com.sigrap.employee;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
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
    employee.setStatus(EmployeeStatus.ACTIVE);
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
    employeeRepository.deleteAllById(ids);
  }

  /**
   * Activates an employee's account.
   *
   * @param id The ID of the employee to activate
   * @return EmployeeInfo containing the updated employee's information
   * @throws EntityNotFoundException if the employee is not found
   */
  @Transactional
  public EmployeeInfo activate(Long id) {
    Employee employee = employeeRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Employee not found: " + id)
      );

    if (employee.getStatus() == EmployeeStatus.TERMINATED) {
      throw new IllegalStateException("Cannot activate a terminated employee");
    }

    employee.setStatus(EmployeeStatus.ACTIVE);
    employee = employeeRepository.save(employee);
    return employeeMapper.toInfo(employee);
  }

  /**
   * Deactivates an employee's account.
   *
   * @param id The ID of the employee to deactivate
   * @return EmployeeInfo containing the updated employee's information
   * @throws EntityNotFoundException if the employee is not found
   */
  @Transactional
  public EmployeeInfo deactivate(Long id) {
    Employee employee = employeeRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Employee not found: " + id)
      );

    if (employee.getStatus() == EmployeeStatus.TERMINATED) {
      throw new IllegalStateException(
        "Cannot deactivate a terminated employee"
      );
    }

    employee.setStatus(EmployeeStatus.INACTIVE);
    employee = employeeRepository.save(employee);
    return employeeMapper.toInfo(employee);
  }

  /**
   * Terminates an employee's employment.
   *
   * @param id The ID of the employee to terminate
   * @return EmployeeInfo containing the updated employee's information
   * @throws EntityNotFoundException if the employee is not found
   */
  @Transactional
  public EmployeeInfo terminate(Long id) {
    Employee employee = employeeRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Employee not found: " + id)
      );

    employee.setStatus(EmployeeStatus.TERMINATED);
    employee.setTerminationDate(LocalDateTime.now());
    employee = employeeRepository.save(employee);
    return employeeMapper.toInfo(employee);
  }

  /**
   * Finds all employees with a specific status.
   *
   * @param status The status to search for
   * @return List of EmployeeInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<EmployeeInfo> findByStatus(EmployeeStatus status) {
    List<Employee> employees = employeeRepository.findByStatus(status);
    return employeeMapper.toInfoList(employees);
  }

  /**
   * Finds all employees hired between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of EmployeeInfo DTOs
   */
  @Transactional(readOnly = true)
  public List<EmployeeInfo> findByHireDateBetween(
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    List<Employee> employees = employeeRepository.findByHireDateBetween(
      startDate,
      endDate
    );
    return employeeMapper.toInfoList(employees);
  }

  /**
   * Finds an employee by their document ID.
   *
   * @param documentId The document ID to search for
   * @return EmployeeInfo containing the employee's information
   * @throws EntityNotFoundException if the employee is not found
   */
  @Transactional(readOnly = true)
  public EmployeeInfo findByDocumentId(String documentId) {
    Employee employee = employeeRepository
      .findByDocumentId(documentId)
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Employee not found with document ID: " + documentId
        )
      );
    return employeeMapper.toInfo(employee);
  }

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

    if (data.getHireDate() == null) {
      throw new IllegalArgumentException("Hire date is required");
    }

    if (data.getHireDate().isAfter(LocalDateTime.now())) {
      throw new IllegalArgumentException("Hire date cannot be in the future");
    }
  }
}
