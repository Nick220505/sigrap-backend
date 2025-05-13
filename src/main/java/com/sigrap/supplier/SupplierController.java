package com.sigrap.supplier;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing suppliers.
 * Provides endpoints for CRUD operations on suppliers.
 *
 * <p>This controller manages:
 * <ul>
 *   <li>Supplier creation and validation</li>
 *   <li>Supplier retrieval (single and bulk)</li>
 *   <li>Supplier updates</li>
 *   <li>Supplier deletion (single and bulk)</li>
 * </ul></p>
 *
 * <p>Suppliers are crucial to the inventory management system:
 * <ul>
 *   <li>They provide products to the business</li>
 *   <li>Their details are needed for ordering and payment</li>
 *   <li>Their performance affects inventory management</li>
 * </ul></p>
 *
 * <p>Usage Examples:
 * <pre>
 * // Create supplier
 * POST /api/suppliers
 * {
 *   "name": "Office Depot",
 *   "phone": "123-456-7890",
 *   "email": "contact@officedepot.com"
 * }
 *
 * // Update supplier
 * PUT /api/suppliers/1
 * {
 *   "name": "Office Depot Inc.",
 *   "phone": "123-456-7890",
 *   "email": "contact@officedepot.com"
 * }
 * </pre></p>
 *
 * @see SupplierService
 * @see Supplier
 */
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(
  name = "Supplier Management",
  description = "Operations for managing suppliers"
)
public class SupplierController {

  /**
   * Service for handling supplier business logic.
   * Provides operations for creating, retrieving, updating, and deleting suppliers.
   */
  private final SupplierService supplierService;

  /**
   * Retrieves all suppliers in the system.
   *
   * <p>This endpoint provides a complete list of all available suppliers,
   * which can be used for:
   * <ul>
   *   <li>Populating supplier selection dropdowns</li>
   *   <li>Supplier management interfaces</li>
   *   <li>Order placement</li>
   * </ul></p>
   *
   * @return List of all suppliers with their details
   */
  @Operation(
    summary = "Get all suppliers",
    description = "Retrieves a list of all suppliers"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Suppliers retrieved successfully"
  )
  @GetMapping
  public List<SupplierInfo> findAll() {
    return supplierService.findAll();
  }

  /**
   * Retrieves a specific supplier by its ID.
   *
   * <p>This endpoint is useful for:
   * <ul>
   *   <li>Viewing supplier details</li>
   *   <li>Pre-populating edit forms</li>
   *   <li>Verifying supplier existence</li>
   * </ul></p>
   *
   * @param id The unique identifier of the supplier
   * @return The supplier information
   * @throws EntityNotFoundException if supplier not found
   */
  @Operation(
    summary = "Get supplier by ID",
    description = "Retrieves a supplier by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "Supplier found"),
      @ApiResponse(
        responseCode = "404",
        description = "Supplier not found",
        content = @Content
      ),
    }
  )
  @GetMapping("/{id}")
  public SupplierInfo findById(
    @Parameter(
      description = "ID of the supplier to retrieve"
    ) @PathVariable Long id
  ) {
    return supplierService.findById(id);
  }

  /**
   * Creates a new supplier.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Validates supplier data</li>
   *   <li>Creates new supplier record</li>
   *   <li>Returns the created supplier</li>
   * </ul></p>
   *
   * <p>Required fields:
   * <ul>
   *   <li>name - Supplier name</li>
   * </ul></p>
   *
   * @param supplierData The supplier data to create
   * @return The created supplier information
   */
  @Operation(
    summary = "Create a new supplier",
    description = "Creates a new supplier"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "201",
        description = "Supplier created successfully"
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content
      ),
    }
  )
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SupplierInfo create(
    @Parameter(
      description = "Supplier data to create",
      required = true
    ) @Valid @RequestBody SupplierData supplierData
  ) {
    return supplierService.create(supplierData);
  }

  /**
   * Updates an existing supplier.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Validates update data</li>
   *   <li>Updates supplier record</li>
   *   <li>Returns updated supplier</li>
   * </ul></p>
   *
   * <p>Updatable fields:
   * <ul>
   *   <li>All supplier properties</li>
   * </ul></p>
   *
   * @param id The ID of the supplier to update
   * @param supplierData The updated supplier data
   * @return The updated supplier information
   * @throws EntityNotFoundException if supplier not found
   */
  @Operation(
    summary = "Update a supplier",
    description = "Updates an existing supplier by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Supplier updated successfully"
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data",
        content = @Content
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Supplier not found",
        content = @Content
      ),
    }
  )
  @PutMapping("/{id}")
  public SupplierInfo update(
    @Parameter(
      description = "ID of the supplier to update"
    ) @PathVariable Long id,
    @Parameter(
      description = "Updated supplier data",
      required = true
    ) @Valid @RequestBody SupplierData supplierData
  ) {
    return supplierService.update(id, supplierData);
  }

  /**
   * Deletes a supplier by its ID.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Verifies supplier existence</li>
   *   <li>Removes the supplier</li>
   * </ul></p>
   *
   * @param id The ID of the supplier to delete
   * @throws EntityNotFoundException if supplier not found
   */
  @Operation(
    summary = "Delete a supplier",
    description = "Deletes a supplier by its ID"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "204",
        description = "Supplier deleted successfully"
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Supplier not found",
        content = @Content
      ),
    }
  )
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
    @Parameter(
      description = "ID of the supplier to delete"
    ) @PathVariable Long id
  ) {
    supplierService.delete(id);
  }

  /**
   * Deletes multiple suppliers by their IDs.
   *
   * <p>This endpoint:
   * <ul>
   *   <li>Validates all supplier IDs</li>
   *   <li>Performs bulk deletion</li>
   * </ul></p>
   *
   * <p>Note: The operation will fail if any supplier does not exist.</p>
   *
   * @param ids List of supplier IDs to delete
   * @throws EntityNotFoundException if any supplier not found
   */
  @Operation(
    summary = "Delete multiple suppliers",
    description = "Deletes multiple suppliers by their IDs"
  )
  @ApiResponse(
    responseCode = "204",
    description = "Suppliers deleted successfully"
  )
  @DeleteMapping("/delete-many")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAllById(
    @Parameter(
      description = "List of supplier IDs to delete",
      required = true
    ) @RequestBody List<Long> ids
  ) {
    supplierService.deleteAllById(ids);
  }
}
