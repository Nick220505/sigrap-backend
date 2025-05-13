package com.sigrap.employee;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Mapper class for converting between Employee entities and DTOs.
 * Handles the conversion of data between different representations.
 *
 * <p>This class provides methods for:
 * <ul>
 *   <li>Converting entities to DTOs</li>
 *   <li>Converting DTOs to entities</li>
 *   <li>Updating entities from DTOs</li>
 *   <li>Batch conversions</li>
 * </ul></p>
 */
@Component
@RequiredArgsConstructor
public class EmployeeMapper {

  private final UserRepository userRepository;

  /**
   * Converts an Employee entity to EmployeeInfo DTO.
   *
   * @param employee The employee entity to convert
   * @return EmployeeInfo containing the employee's information
   */
  public EmployeeInfo toInfo(Employee employee) {
    if (employee == null) {
      return null;
    }

    return EmployeeInfo.builder()
      .id(employee.getId())
      .userId(employee.getUser().getId())
      .firstName(employee.getFirstName())
      .lastName(employee.getLastName())
      .documentId(employee.getDocumentId())
      .phoneNumber(employee.getPhoneNumber())
      .email(employee.getEmail())
      .position(employee.getPosition())
      .department(employee.getDepartment())
      .hireDate(employee.getHireDate())
      .terminationDate(employee.getTerminationDate())
      .status(employee.getStatus())
      .profileImageUrl(employee.getProfileImageUrl())
      .createdAt(employee.getCreatedAt())
      .updatedAt(employee.getUpdatedAt())
      .build();
  }

  /**
   * Converts a list of Employee entities to a list of EmployeeInfo DTOs.
   *
   * @param employees List of employee entities to convert
   * @return List of EmployeeInfo DTOs
   */
  public List<EmployeeInfo> toInfoList(List<Employee> employees) {
    if (employees == null) {
      return Collections.emptyList();
    }
    return employees
      .stream()
      .filter(employee -> employee != null)
      .map(this::toInfo)
      .toList();
  }

  /**
   * Creates a new Employee entity from EmployeeData DTO.
   *
   * @param employeeData The DTO containing employee data
   * @return New Employee entity
   * @throws EntityNotFoundException if referenced user is not found
   */
  public Employee toEntity(EmployeeData employeeData) {
    if (employeeData == null) {
      return null;
    }

    User user = userRepository
      .findById(employeeData.getUserId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "User not found: " + employeeData.getUserId()
        )
      );

    return Employee.builder()
      .user(user)
      .firstName(employeeData.getFirstName())
      .lastName(employeeData.getLastName())
      .documentId(employeeData.getDocumentId())
      .phoneNumber(employeeData.getPhoneNumber())
      .email(employeeData.getEmail())
      .position(employeeData.getPosition())
      .department(employeeData.getDepartment())
      .hireDate(employeeData.getHireDate())
      .profileImageUrl(employeeData.getProfileImageUrl())
      .status(Employee.EmployeeStatus.ACTIVE)
      .build();
  }

  /**
   * Updates an existing Employee entity with data from EmployeeData DTO.
   *
   * @param employee The employee entity to update
   * @param employeeData The DTO containing new employee data
   * @throws EntityNotFoundException if referenced user is not found
   */
  public void updateEntityFromData(
    Employee employee,
    EmployeeData employeeData
  ) {
    if (employeeData == null) {
      return;
    }

    if (
      employeeData.getUserId() != null &&
      !employeeData.getUserId().equals(employee.getUser().getId())
    ) {
      User user = userRepository
        .findById(employeeData.getUserId())
        .orElseThrow(() ->
          new EntityNotFoundException(
            "User not found: " + employeeData.getUserId()
          )
        );
      employee.setUser(user);
    }

    if (employeeData.getFirstName() != null) {
      employee.setFirstName(employeeData.getFirstName());
    }
    if (employeeData.getLastName() != null) {
      employee.setLastName(employeeData.getLastName());
    }
    if (employeeData.getDocumentId() != null) {
      employee.setDocumentId(employeeData.getDocumentId());
    }
    if (employeeData.getPhoneNumber() != null) {
      employee.setPhoneNumber(employeeData.getPhoneNumber());
    }
    if (employeeData.getEmail() != null) {
      employee.setEmail(employeeData.getEmail());
    }
    if (employeeData.getPosition() != null) {
      employee.setPosition(employeeData.getPosition());
    }
    if (employeeData.getDepartment() != null) {
      employee.setDepartment(employeeData.getDepartment());
    }
    if (employeeData.getHireDate() != null) {
      employee.setHireDate(employeeData.getHireDate());
    }
    if (employeeData.getProfileImageUrl() != null) {
      employee.setProfileImageUrl(employeeData.getProfileImageUrl());
    }
  }

  public String getFullName(Employee employee) {
    if (employee == null) {
      return null;
    }
    return employee.getFirstName() + " " + employee.getLastName();
  }
}
