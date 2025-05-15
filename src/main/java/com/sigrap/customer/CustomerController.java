package com.sigrap.customer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * REST controller for managing customers.
 * Provides endpoints for customer operations.
 */
@RestController
@RequestMapping("/api/customers")
@Tag(
  name = "Customer Management",
  description = "Endpoints for managing customers"
)
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  /**
   * Retrieves all customers.
   *
   * @return List of all customer information
   */
  @GetMapping
  @Operation(
    summary = "Find all customers",
    description = "Retrieves a list of all customers in the system",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved customers"
      ),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
    }
  )
  public List<CustomerInfo> findAll() {
    return customerService.findAll();
  }

  /**
   * Finds a customer by ID.
   *
   * @param id The ID of the customer to retrieve
   * @return The customer information
   */
  @GetMapping("/{id}")
  @Operation(
    summary = "Find customer by ID",
    description = "Retrieves a single customer by their ID",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved customer"
      ),
      @ApiResponse(responseCode = "404", description = "Customer not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
    }
  )
  public CustomerInfo findById(
    @Parameter(
      description = "ID of the customer to retrieve",
      required = true
    ) @PathVariable Long id
  ) {
    return customerService.findById(id);
  }

  /**
   * Creates a new customer.
   *
   * @param customerData The data for the new customer
   * @return The created customer information
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Create a new customer",
    description = "Creates a new customer with the provided data",
    responses = {
      @ApiResponse(
        responseCode = "201",
        description = "Customer successfully created"
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or customer with email already exists"
      ),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
    }
  )
  public CustomerInfo create(
    @Parameter(
      description = "Data for creating the new customer",
      required = true
    ) @Valid @RequestBody CustomerData customerData
  ) {
    return customerService.create(customerData);
  }

  /**
   * Updates an existing customer.
   *
   * @param id The ID of the customer to update
   * @param customerData The new data for the customer
   * @return The updated customer information
   */
  @PutMapping("/{id}")
  @Operation(
    summary = "Update a customer",
    description = "Updates an existing customer with the provided data",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Customer successfully updated"
      ),
      @ApiResponse(responseCode = "404", description = "Customer not found"),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data or email already exists"
      ),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
    }
  )
  public CustomerInfo update(
    @Parameter(
      description = "ID of the customer to update",
      required = true
    ) @PathVariable Long id,
    @Parameter(
      description = "Updated customer data",
      required = true
    ) @Valid @RequestBody CustomerData customerData
  ) {
    return customerService.update(id, customerData);
  }

  /**
   * Deletes a customer.
   *
   * @param id The ID of the customer to delete
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Delete a customer",
    description = "Deletes a customer by their ID",
    responses = {
      @ApiResponse(
        responseCode = "204",
        description = "Customer successfully deleted"
      ),
      @ApiResponse(responseCode = "404", description = "Customer not found"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
    }
  )
  public void delete(
    @Parameter(
      description = "ID of the customer to delete",
      required = true
    ) @PathVariable Long id
  ) {
    customerService.delete(id);
  }

  /**
   * Updates the status of a customer.
   *
   * @param id The ID of the customer
   * @param status The new status to set
   * @return The updated customer information
   */
  @PutMapping("/{id}/status")
  @Operation(
    summary = "Update customer status",
    description = "Updates the status of an existing customer",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Status successfully updated"
      ),
      @ApiResponse(responseCode = "404", description = "Customer not found"),
      @ApiResponse(responseCode = "400", description = "Invalid status"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
    }
  )
  public CustomerInfo updateStatus(
    @Parameter(
      description = "ID of the customer",
      required = true
    ) @PathVariable Long id,
    @Parameter(
      description = "New status value",
      required = true
    ) @RequestParam CustomerStatus status
  ) {
    return customerService.updateStatus(id, status);
  }

  /**
   * Finds customers by status.
   *
   * @param status The status to filter by
   * @return List of customers with the specified status
   */
  @GetMapping("/status/{status}")
  @Operation(
    summary = "Find customers by status",
    description = "Retrieves all customers with the specified status",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved customers"
      ),
      @ApiResponse(responseCode = "400", description = "Invalid status"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
    }
  )
  public List<CustomerInfo> findByStatus(
    @Parameter(
      description = "Status to filter by",
      required = true
    ) @PathVariable CustomerStatus status
  ) {
    return customerService.findByStatus(status);
  }

  /**
   * Searches for customers by name.
   *
   * @param query The search term to match against customer names
   * @return List of matching customers
   */
  @GetMapping("/search")
  @Operation(
    summary = "Search customers by name",
    description = "Searches for customers whose first name or last name contains the search term",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Search completed successfully"
      ),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
    }
  )
  public List<CustomerInfo> searchByName(
    @Parameter(
      description = "Search term to match against names",
      required = true
    ) @RequestParam String query
  ) {
    return customerService.searchByName(query);
  }

  /**
   * Finds customers created within a date range.
   *
   * @param startDate The start date (inclusive)
   * @param endDate The end date (inclusive)
   * @return List of customers created within the date range
   */
  @GetMapping("/created-between")
  @Operation(
    summary = "Find customers created within date range",
    description = "Retrieves customers created between the specified start and end dates",
    responses = {
      @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved customers"
      ),
      @ApiResponse(responseCode = "400", description = "Invalid date format"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Forbidden"),
    }
  )
  public List<CustomerInfo> findByCreatedDateRange(
    @Parameter(
      description = "Start date (yyyy-MM-dd'T'HH:mm:ss)",
      required = true
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(
      description = "End date (yyyy-MM-dd'T'HH:mm:ss)",
      required = true
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate
  ) {
    return customerService.findByCreatedDateRange(startDate, endDate);
  }
}
