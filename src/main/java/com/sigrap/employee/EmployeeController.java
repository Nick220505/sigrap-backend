package com.sigrap.employee;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for employee management operations.
 * Provides endpoints for employee-related functionality.
 *
 * <p>This controller includes endpoints for:
 * <ul>
 *   <li>Employee CRUD operations</li>
 *   <li>Status management</li>
 *   <li>Department operations</li>
 *   <li>Employee search functionality</li>
 * </ul></p>
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(
  name = "Employee Management",
  description = "Endpoints for managing employees"
)
public class EmployeeController {

  private final EmployeeService employeeService;

  /**
   * Retrieves all employees.
   *
   * @return List of EmployeeInfo DTOs
   */
  @GetMapping
  @Operation(
    summary = "Get all employees",
    description = "Retrieves a list of all employees in the system"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved list of employees"
  )
  public List<EmployeeInfo> findAll() {
    return employeeService.findAll();
  }

  /**
   * Retrieves an employee by their ID.
   *
   * @param id The ID of the employee to retrieve
   * @return EmployeeInfo containing the employee's information
   */
  @GetMapping("/{id}")
  @Operation(
    summary = "Get employee by ID",
    description = "Retrieves an employee's information by their ID"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved employee information"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Employee not found with the given ID"
  )
  public EmployeeInfo findById(
    @Parameter(
      description = "ID of the employee to retrieve"
    ) @PathVariable Long id
  ) {
    return employeeService.findById(id);
  }

  /**
   * Creates a new employee.
   *
   * @param employeeData The data for the new employee
   * @return EmployeeInfo containing the created employee's information
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Create new employee",
    description = "Creates a new employee with the provided information"
  )
  @ApiResponse(
    responseCode = "201",
    description = "Successfully created new employee"
  )
  @ApiResponse(
    responseCode = "400",
    description = "Invalid employee data provided"
  )
  public EmployeeInfo create(
    @Parameter(
      description = "Employee data for creation"
    ) @Valid @RequestBody EmployeeData employeeData
  ) {
    return employeeService.create(employeeData);
  }

  /**
   * Updates an existing employee.
   *
   * @param id The ID of the employee to update
   * @param employeeData The new data for the employee
   * @return EmployeeInfo containing the updated employee's information
   */
  @PutMapping("/{id}")
  @Operation(
    summary = "Update employee",
    description = "Updates an existing employee's information"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully updated employee information"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Employee not found with the given ID"
  )
  public EmployeeInfo update(
    @Parameter(
      description = "ID of the employee to update"
    ) @PathVariable Long id,
    @Parameter(
      description = "Updated employee data"
    ) @Valid @RequestBody EmployeeData employeeData
  ) {
    return employeeService.update(id, employeeData);
  }

  /**
   * Deletes an employee.
   *
   * @param id The ID of the employee to delete
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Delete employee",
    description = "Deletes an employee from the system"
  )
  @ApiResponse(
    responseCode = "204",
    description = "Successfully deleted employee"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Employee not found with the given ID"
  )
  public void delete(
    @Parameter(
      description = "ID of the employee to delete"
    ) @PathVariable Long id
  ) {
    employeeService.delete(id);
  }

  /**
   * Deletes multiple employees by their IDs.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Validates all employee IDs</li>
   *   <li>Performs bulk deletion</li>
   *   <li>Updates employee records</li>
   * </ul></p>
   *
   * <p>Note: This operation:
   * <ul>
   *   <li>Is atomic - all employees are deleted or none</li>
   *   <li>Is irreversible - consider deactivating if historical data is needed</li>
   *   <li>Will fail if any employee doesn't exist</li>
   * </ul></p>
   *
   * @param ids List of employee IDs to delete
   * @throws EntityNotFoundException if any employee not found
   */
  @DeleteMapping("/delete-many")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Delete multiple employees",
    description = "Deletes multiple employees by their IDs"
  )
  @ApiResponse(
    responseCode = "204",
    description = "Employees deleted successfully"
  )
  public void deleteAllById(
    @Parameter(
      description = "List of employee IDs to delete",
      required = true
    ) @RequestBody List<Long> ids
  ) {
    employeeService.deleteAllById(ids);
  }

  /**
   * Finds all employees hired between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of EmployeeInfo DTOs
   */
  @GetMapping("/hired-between")
  @Operation(
    summary = "Find employees by hire date range",
    description = "Retrieves all employees hired between two dates"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved employees"
  )
  public List<EmployeeInfo> findByHireDateBetween(
    @Parameter(
      description = "Start date of the range"
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(
      description = "End date of the range"
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate
  ) {
    return employeeService.findByHireDateBetween(startDate, endDate);
  }
}
