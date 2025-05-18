package com.sigrap.sale;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
 * REST controller for managing sales.
 */
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales Management", description = "Operations for managing sales")
public class SaleController {

  private final SaleService saleService;

  /**
   * Get all sales.
   *
   * @return List of all sales as SaleInfo DTOs
   */
  @GetMapping
  @Operation(
    summary = "Get all sales",
    description = "Retrieves a list of all sales"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Sales retrieved successfully"
  )
  public List<SaleInfo> findAll() {
    return saleService.findAll();
  }

  /**
   * Get a sale by its ID.
   *
   * @param id The ID of the sale to retrieve
   * @return The sale as a SaleInfo DTO
   */
  @GetMapping("/{id}")
  @Operation(
    summary = "Get sale by ID",
    description = "Retrieves a sale by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "Sale found"),
      @ApiResponse(responseCode = "404", description = "Sale not found"),
    }
  )
  public SaleInfo findById(
    @Parameter(
      description = "ID of the sale to retrieve"
    ) @PathVariable Integer id
  ) {
    return saleService.findById(id);
  }

  /**
   * Get sales by employee ID.
   *
   * @param employeeId The ID of the employee who processed the sales
   * @return List of sales processed by the given employee as SaleInfo DTOs
   */
  @GetMapping("/employee/{employeeId}")
  @Operation(
    summary = "Get sales by employee ID",
    description = "Retrieves sales processed by the given employee"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Sales retrieved successfully"
      ),
      @ApiResponse(responseCode = "404", description = "Employee not found"),
    }
  )
  public List<SaleInfo> findByEmployeeId(
    @Parameter(description = "ID of the employee") @PathVariable Long employeeId
  ) {
    return saleService.findByEmployeeId(employeeId);
  }

  /**
   * Get sales by customer ID.
   *
   * @param customerId The ID of the customer who made the purchases
   * @return List of sales made by the given customer as SaleInfo DTOs
   */
  @GetMapping("/customer/{customerId}")
  @Operation(
    summary = "Get sales by customer ID",
    description = "Retrieves sales made by the given customer"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Sales retrieved successfully"
      ),
      @ApiResponse(responseCode = "404", description = "Customer not found"),
    }
  )
  public List<SaleInfo> findByCustomerId(
    @Parameter(description = "ID of the customer") @PathVariable Long customerId
  ) {
    return saleService.findByCustomerId(customerId);
  }

  /**
   * Get sales by date range.
   *
   * @param startDate The start date (inclusive)
   * @param endDate   The end date (inclusive)
   * @return List of sales created within the given date range as SaleInfo DTOs
   */
  @GetMapping("/created-between")
  @Operation(
    summary = "Get sales by date range",
    description = "Retrieves sales created between the given dates"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Sales retrieved successfully"
  )
  public List<SaleInfo> findByCreatedDateRange(
    @Parameter(
      description = "Start date (yyyy-MM-dd'T'HH:mm:ss)"
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime startDate,
    @Parameter(
      description = "End date (yyyy-MM-dd'T'HH:mm:ss)"
    ) @RequestParam @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE_TIME
    ) LocalDateTime endDate
  ) {
    return saleService.findByCreatedDateRange(startDate, endDate);
  }

  /**
   * Create a new sale.
   *
   * @param saleData The data for the new sale
   * @return The created sale as a SaleInfo DTO
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a new sale", description = "Creates a new sale")
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "201",
        description = "Sale created successfully"
      ),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(
        responseCode = "404",
        description = "Customer or employee not found"
      ),
    }
  )
  public SaleInfo create(
    @Parameter(
      description = "Sale data to create",
      required = true
    ) @Valid @RequestBody SaleData saleData
  ) {
    return saleService.create(saleData);
  }

  /**
   * Update an existing sale.
   *
   * @param id       The ID of the sale to update
   * @param saleData The new data for the sale
   * @return The updated sale as a SaleInfo DTO
   */
  @PutMapping("/{id}")
  @Operation(
    summary = "Update a sale",
    description = "Updates an existing sale"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Sale updated successfully"
      ),
      @ApiResponse(responseCode = "400", description = "Invalid input data"),
      @ApiResponse(
        responseCode = "404",
        description = "Sale, customer, or employee not found"
      ),
    }
  )
  public SaleInfo update(
    @Parameter(
      description = "ID of the sale to update"
    ) @PathVariable Integer id,
    @Parameter(
      description = "Updated sale data",
      required = true
    ) @Valid @RequestBody SaleData saleData
  ) {
    return saleService.update(id, saleData);
  }

  /**
   * Delete a sale.
   *
   * @param id The ID of the sale to delete
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a sale", description = "Deletes a sale")
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "204",
        description = "Sale deleted successfully"
      ),
      @ApiResponse(responseCode = "404", description = "Sale not found"),
    }
  )
  public void delete(
    @Parameter(
      description = "ID of the sale to delete"
    ) @PathVariable Integer id
  ) {
    saleService.delete(id);
  }

  /**
   * Deletes multiple sales by their IDs.
   *
   * @param ids List of sale IDs to delete
   */
  @DeleteMapping("/delete-many")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Delete multiple sales",
    description = "Deletes multiple sales by their IDs"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "204",
        description = "Sales deleted successfully"
      ),
      @ApiResponse(
        responseCode = "404",
        description = "One or more sales not found",
        content = @Content
      ),
    }
  )
  public void deleteAllById(
    @Parameter(
      description = "List of sale IDs to delete",
      required = true
    ) @RequestBody List<Integer> ids
  ) {
    saleService.deleteAllById(ids);
  }
}
