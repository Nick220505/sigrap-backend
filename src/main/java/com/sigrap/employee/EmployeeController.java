package com.sigrap.employee;

import java.time.LocalDateTime;
import java.util.List;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
   * Activates an employee's account.
   *
   * @param id The ID of the employee to activate
   * @return EmployeeInfo containing the updated employee's information
   */
  @PutMapping("/{id}/activate")
  @Operation(
    summary = "Activate employee",
    description = "Activates an employee's account"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully activated employee"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Employee not found with the given ID"
  )
  public EmployeeInfo activate(
    @Parameter(
      description = "ID of the employee to activate"
    ) @PathVariable Long id
  ) {
    return employeeService.activate(id);
  }

  /**
   * Deactivates an employee's account.
   *
   * @param id The ID of the employee to deactivate
   * @return EmployeeInfo containing the updated employee's information
   */
  @PutMapping("/{id}/deactivate")
  @Operation(
    summary = "Deactivate employee",
    description = "Deactivates an employee's account"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully deactivated employee"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Employee not found with the given ID"
  )
  public EmployeeInfo deactivate(
    @Parameter(
      description = "ID of the employee to deactivate"
    ) @PathVariable Long id
  ) {
    return employeeService.deactivate(id);
  }

  /**
   * Terminates an employee's employment.
   *
   * @param id The ID of the employee to terminate
   * @return EmployeeInfo containing the updated employee's information
   */
  @PutMapping("/{id}/terminate")
  @Operation(
    summary = "Terminate employee",
    description = "Terminates an employee's employment"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully terminated employee"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Employee not found with the given ID"
  )
  public EmployeeInfo terminate(
    @Parameter(
      description = "ID of the employee to terminate"
    ) @PathVariable Long id
  ) {
    return employeeService.terminate(id);
  }

  /**
   * Finds all employees in a specific department.
   *
   * @param department The department to search for
   * @return List of EmployeeInfo DTOs
   */
  @GetMapping("/department/{department}")
  @Operation(
    summary = "Find employees by department",
    description = "Retrieves all employees in a specific department"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved employees"
  )
  public List<EmployeeInfo> findByDepartment(
    @Parameter(
      description = "Department to search for"
    ) @PathVariable String department
  ) {
    return employeeService.findByDepartment(department);
  }

  /**
   * Finds all employees with a specific status.
   *
   * @param status The status to search for
   * @return List of EmployeeInfo DTOs
   */
  @GetMapping("/status/{status}")
  @Operation(
    summary = "Find employees by status",
    description = "Retrieves all employees with a specific status"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved employees"
  )
  public List<EmployeeInfo> findByStatus(
    @Parameter(
      description = "Status to search for"
    ) @PathVariable Employee.EmployeeStatus status
  ) {
    return employeeService.findByStatus(status);
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

  /**
   * Finds all employees in a specific department with a specific status.
   *
   * @param department The department to search for
   * @param status The status to search for
   * @return List of EmployeeInfo DTOs
   */
  @GetMapping("/department/{department}/status/{status}")
  @Operation(
    summary = "Find employees by department and status",
    description = "Retrieves all employees in a specific department with a specific status"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved employees"
  )
  public List<EmployeeInfo> findByDepartmentAndStatus(
    @Parameter(
      description = "Department to search for"
    ) @PathVariable String department,
    @Parameter(
      description = "Status to search for"
    ) @PathVariable Employee.EmployeeStatus status
  ) {
    return employeeService.findByDepartmentAndStatus(department, status);
  }
}
