package com.sigrap.employee;

import java.math.BigDecimal;
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
 * REST controller for employee performance management operations.
 * Provides endpoints for performance-related functionality.
 *
 * <p>This controller includes endpoints for:
 * <ul>
 *   <li>Performance CRUD operations</li>
 *   <li>Performance metrics calculation</li>
 *   <li>Performance reporting</li>
 *   <li>Top performers identification</li>
 * </ul></p>
 */
@RestController
@RequestMapping("/api/employee-performance")
@Tag(
  name = "Employee Performance",
  description = "Employee performance management endpoints"
)
@RequiredArgsConstructor
public class EmployeePerformanceController {

  private final EmployeePerformanceService performanceService;

  /**
   * Retrieves all performance records.
   *
   * @return List of EmployeePerformanceInfo DTOs
   */
  @GetMapping
  @Operation(
    summary = "Get all performance records",
    description = "Retrieves a list of all performance records in the system"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved list of performance records"
  )
  public List<EmployeePerformanceInfo> findAll() {
    return performanceService.findAll();
  }

  /**
   * Retrieves a performance record by its ID.
   *
   * @param id The ID of the performance record to retrieve
   * @return EmployeePerformanceInfo containing the performance's information
   */
  @GetMapping("/{id}")
  @Operation(
    summary = "Get a performance record by ID",
    description = "Retrieves a performance record's information by its ID"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved performance record"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Performance record not found"
  )
  public EmployeePerformanceInfo findById(
    @Parameter(
      description = "ID of the performance record"
    ) @PathVariable Long id
  ) {
    return performanceService.findById(id);
  }

  /**
   * Finds all performance records for a specific employee.
   *
   * @param employeeId The ID of the employee
   * @return List of EmployeePerformanceInfo DTOs
   */
  @GetMapping("/employee/{employeeId}")
  @Operation(
    summary = "Get all performance records for an employee",
    description = "Retrieves all performance records for a specific employee"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved performance records"
  )
  public List<EmployeePerformanceInfo> findByEmployeeId(
    @Parameter(description = "ID of the employee") @PathVariable Long employeeId
  ) {
    return performanceService.findByEmployeeId(employeeId);
  }

  /**
   * Finds performance records for a specific employee within a date range.
   *
   * @param employeeId The ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of EmployeePerformanceInfo DTOs
   */
  @GetMapping("/employee/{employeeId}/period")
  @Operation(
    summary = "Get performance records for an employee within a date range",
    description = "Retrieves performance records for a specific employee within a date range"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved performance records"
  )
  public List<EmployeePerformanceInfo> findByEmployeeAndPeriod(
    @Parameter(
      description = "ID of the employee"
    ) @PathVariable Long employeeId,
    @Parameter(
      description = "Start date of the period (ISO format)"
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(
      description = "End date of the period (ISO format)"
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate
  ) {
    return performanceService.findByEmployeeAndPeriod(
      employeeId,
      startDate,
      endDate
    );
  }

  /**
   * Finds top performers within a date range.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @param limit Maximum number of results to return
   * @return List of EmployeePerformanceInfo DTOs
   */
  @GetMapping("/top-performers")
  @Operation(
    summary = "Get top performers within a date range",
    description = "Retrieves top performers within a date range"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved top performers"
  )
  public List<EmployeePerformanceInfo> findTopPerformers(
    @Parameter(description = "Start date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(description = "End date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate,
    @RequestParam(defaultValue = "10") Integer limit
  ) {
    return performanceService.findTopPerformers(startDate, endDate, limit);
  }

  /**
   * Finds performance records by minimum rating within a date range.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @param minRating Minimum rating to filter by
   * @return List of EmployeePerformanceInfo DTOs
   */
  @GetMapping("/by-rating")
  @Operation(
    summary = "Get performance records by minimum rating within a date range",
    description = "Retrieves performance records by minimum rating within a date range"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved performance records"
  )
  public List<EmployeePerformanceInfo> findByPeriodAndMinimumRating(
    @Parameter(
      description = "Start date of the period (ISO format)"
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(
      description = "End date of the period (ISO format)"
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate,
    @Parameter(
      description = "Minimum rating to filter by"
    ) @RequestParam Integer minRating
  ) {
    return performanceService.findByPeriodAndMinimumRating(
      startDate,
      endDate,
      minRating
    );
  }

  /**
   * Creates a new performance record.
   *
   * @param data The data for the new performance record
   * @return EmployeePerformanceInfo containing the created performance's information
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Create a new performance record",
    description = "Creates a new performance record with the provided information"
  )
  @ApiResponse(
    responseCode = "201",
    description = "Successfully created new performance record"
  )
  @ApiResponse(
    responseCode = "400",
    description = "Invalid performance data provided"
  )
  public EmployeePerformanceInfo create(
    @Parameter(
      description = "Performance data to create"
    ) @Valid @RequestBody EmployeePerformanceData data
  ) {
    return performanceService.create(data);
  }

  /**
   * Updates an existing performance record.
   *
   * @param id The ID of the performance record to update
   * @param data The new data for the performance record
   * @return EmployeePerformanceInfo containing the updated performance's information
   */
  @PutMapping("/{id}")
  @Operation(
    summary = "Update an existing performance record",
    description = "Updates an existing performance record's information"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully updated performance record"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Performance record not found"
  )
  public EmployeePerformanceInfo update(
    @Parameter(
      description = "ID of the performance record to update"
    ) @PathVariable Long id,
    @Parameter(
      description = "Updated performance data"
    ) @Valid @RequestBody EmployeePerformanceData data
  ) {
    return performanceService.update(id, data);
  }

  /**
   * Deletes a performance record.
   *
   * @param id The ID of the performance record to delete
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Delete a performance record",
    description = "Deletes a performance record from the system"
  )
  @ApiResponse(
    responseCode = "204",
    description = "Successfully deleted performance record"
  )
  @ApiResponse(
    responseCode = "404",
    description = "Performance record not found"
  )
  public void delete(
    @Parameter(
      description = "ID of the performance record to delete"
    ) @PathVariable Long id
  ) {
    performanceService.delete(id);
  }

  /**
   * Generates a performance report for a specific employee between two dates.
   *
   * @param employeeId The ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of EmployeePerformanceInfo DTOs
   */
  @GetMapping("/report")
  @Operation(
    summary = "Generate performance report",
    description = "Generates a performance report for a specific employee between two dates"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully generated performance report"
  )
  public List<EmployeePerformanceInfo> generatePerformanceReport(
    @Parameter(
      description = "ID of the employee"
    ) @RequestParam Long employeeId,
    @Parameter(description = "Start date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(description = "End date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate
  ) {
    return performanceService.generatePerformanceReport(
      employeeId,
      startDate,
      endDate
    );
  }

  /**
   * Calculates total sales for an employee between two dates.
   *
   * @param employeeId The ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return Total sales amount
   */
  @GetMapping("/metrics/sales")
  @Operation(
    summary = "Calculate total sales",
    description = "Calculates total sales for an employee between two dates"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully calculated total sales"
  )
  public BigDecimal calculateTotalSales(
    @Parameter(
      description = "ID of the employee"
    ) @RequestParam Long employeeId,
    @Parameter(description = "Start date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(description = "End date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate
  ) {
    return performanceService.calculateTotalSales(
      employeeId,
      startDate,
      endDate
    );
  }

  /**
   * Calculates average customer satisfaction for an employee between two dates.
   *
   * @param employeeId The ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return Average customer satisfaction
   */
  @GetMapping("/metrics/satisfaction")
  @Operation(
    summary = "Calculate average customer satisfaction",
    description = "Calculates average customer satisfaction for an employee between two dates"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully calculated average customer satisfaction"
  )
  public BigDecimal calculateAverageCustomerSatisfaction(
    @Parameter(
      description = "ID of the employee"
    ) @RequestParam Long employeeId,
    @Parameter(description = "Start date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(description = "End date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate
  ) {
    return performanceService.calculateAverageCustomerSatisfaction(
      employeeId,
      startDate,
      endDate
    );
  }

  /**
   * Calculates total transactions for an employee between two dates.
   *
   * @param employeeId The ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return Total transaction count
   */
  @GetMapping("/metrics/transactions")
  @Operation(
    summary = "Calculate total transactions",
    description = "Calculates total transactions for an employee between two dates"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully calculated total transactions"
  )
  public Integer calculateTotalTransactions(
    @Parameter(
      description = "ID of the employee"
    ) @RequestParam Long employeeId,
    @Parameter(description = "Start date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(description = "End date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate
  ) {
    return performanceService.calculateTotalTransactions(
      employeeId,
      startDate,
      endDate
    );
  }

  /**
   * Finds top performers by sales between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @param limit Maximum number of results to return
   * @return List of EmployeePerformanceInfo DTOs
   */
  @GetMapping("/top/sales")
  @Operation(
    summary = "Find top performers by sales",
    description = "Finds top performers by sales between two dates"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved top performers"
  )
  public List<EmployeePerformanceInfo> findTopPerformersBySales(
    @Parameter(description = "Start date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(description = "End date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate,
    @Parameter(description = "Maximum number of results") @RequestParam(
      defaultValue = "5"
    ) int limit
  ) {
    return performanceService.findTopPerformersBySales(
      startDate,
      endDate,
      limit
    );
  }

  /**
   * Finds top performers by customer satisfaction between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @param limit Maximum number of results to return
   * @return List of EmployeePerformanceInfo DTOs
   */
  @GetMapping("/top/satisfaction")
  @Operation(
    summary = "Find top performers by customer satisfaction",
    description = "Finds top performers by customer satisfaction between two dates"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved top performers"
  )
  public List<EmployeePerformanceInfo> findTopPerformersByCustomerSatisfaction(
    @Parameter(description = "Start date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(description = "End date") @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate,
    @Parameter(description = "Maximum number of results") @RequestParam(
      defaultValue = "5"
    ) int limit
  ) {
    return performanceService.findTopPerformersByCustomerSatisfaction(
      startDate,
      endDate,
      limit
    );
  }
}
