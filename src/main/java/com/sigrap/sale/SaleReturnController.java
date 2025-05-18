package com.sigrap.sale;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * REST controller for managing sales returns.
 *
 * <p>This controller provides endpoints for creating, retrieving, updating,
 * and deleting sales return transactions. It orchestrates interactions
 * with the {@link SaleReturnService} to handle the business logic associated
 * with sales returns, including product stock adjustments.</p>
 *
 * @see SaleReturnService
 * @see SaleReturnInfo
 * @see SaleReturnData
 */
@RestController
@RequestMapping("/api/sale-returns")
@RequiredArgsConstructor
@Tag(
  name = "Sales Returns Management",
  description = "Operations for managing sales returns"
)
public class SaleReturnController {

  private final SaleReturnService saleReturnService;

  /**
   * Creates a new sales return.
   *
   * <p>This endpoint processes a request to create a new sales return.
   * It requires valid sales return data, including details of the original sale,
   * customer, employee, and items being returned. Successful creation results in
   * adjustments to product stock levels.</p>
   *
   * @param saleReturnData The {@link SaleReturnData} DTO containing the data for the new sales return.
   *                       Must be valid according to defined constraints.
   * @return A {@link SaleReturnInfo} DTO representing the newly created sales return.
   * @see SaleReturnService#create(SaleReturnData)
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
    summary = "Create a new sales return",
    description = "Creates a new sales return and adjusts product stock accordingly."
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "201",
        description = "Sales return created successfully"
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data, or validation error (e.g., returning more than purchased)"
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Original sale, customer, employee, or product not found"
      ),
    }
  )
  public SaleReturnInfo create(
    @Parameter(
      description = "Sales return data to create",
      required = true
    ) @Valid @RequestBody SaleReturnData saleReturnData
  ) {
    return saleReturnService.create(saleReturnData);
  }

  /**
   * Updates an existing sales return.
   *
   * <p>This endpoint allows for the modification of an existing sales return identified by its ID.
   * The provided {@link SaleReturnData} will be used to update the return details. This may involve
   * changes to the returned items, reason, or processing employee, and will trigger corresponding
   * adjustments to product stock levels.</p>
   *
   * <p><strong>Note:</strong> The original sale and customer associated with the return cannot be changed.</p>
   *
   * @param id The unique identifier of the sales return to update.
   * @param saleReturnData The {@link SaleReturnData} DTO containing the updated information for the sales return.
   *                       Must be valid according to defined constraints.
   * @return A {@link SaleReturnInfo} DTO representing the state of the sales return after the update.
   * @see SaleReturnService#update(Integer, SaleReturnData)
   */
  @PutMapping("/{id}")
  @Operation(
    summary = "Update an existing sales return",
    description = "Updates an existing sales return and adjusts product stock accordingly."
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Sales return updated successfully"
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Invalid input data, or validation error"
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Sales return, original sale, customer, employee, or product not found"
      ),
    }
  )
  public SaleReturnInfo update(
    @Parameter(
      description = "ID of the sales return to update"
    ) @PathVariable Integer id,
    @Parameter(
      description = "Updated sales return data",
      required = true
    ) @Valid @RequestBody SaleReturnData saleReturnData
  ) {
    return saleReturnService.update(id, saleReturnData);
  }

  /**
   * Retrieves all sales returns currently in the system.
   *
   * @return A list of {@link SaleReturnInfo} DTOs, each representing a sales return.
   *         If no sales returns exist, an empty list is returned.
   * @see SaleReturnService#findAll()
   */
  @GetMapping
  @Operation(
    summary = "Get all sales returns",
    description = "Retrieves a list of all sales returns."
  )
  @ApiResponse(
    responseCode = "200",
    description = "Sales returns retrieved successfully"
  )
  public List<SaleReturnInfo> findAll() {
    return saleReturnService.findAll();
  }

  /**
   * Retrieves a specific sales return by its unique identifier.
   *
   * @param id The ID of the sales return to retrieve.
   * @return A {@link SaleReturnInfo} DTO representing the found sales return.
   * @see SaleReturnService#findById(Integer)
   */
  @GetMapping("/{id}")
  @Operation(
    summary = "Get sales return by ID",
    description = "Retrieves a sales return by its ID."
  )
  @ApiResponses(
    value = {
      @ApiResponse(responseCode = "200", description = "Sales return found"),
      @ApiResponse(
        responseCode = "404",
        description = "Sales return not found"
      ),
    }
  )
  public SaleReturnInfo findById(
    @Parameter(
      description = "ID of the sales return to retrieve"
    ) @PathVariable Integer id
  ) {
    return saleReturnService.findById(id);
  }

  /**
   * Retrieves all sales returns associated with a specific original sale ID.
   *
   * <p>This is useful for finding all return transactions linked to a particular purchase.</p>
   *
   * @param originalSaleId The ID of the original sale.
   * @return A list of {@link SaleReturnInfo} DTOs. If no returns are found for the given
   *         original sale ID, or if the original sale itself doesn't exist, an appropriate response
   *         (potentially an empty list or a 404 if the original sale is not found by the service) is returned.
   * @see SaleReturnService#findByOriginalSaleId(Integer)
   */
  @GetMapping("/original-sale/{originalSaleId}")
  @Operation(
    summary = "Get sales returns by original sale ID",
    description = "Retrieves sales returns associated with a specific original sale ID."
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Sales returns retrieved successfully"
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Original sale not found"
      ),
    }
  )
  public List<SaleReturnInfo> findByOriginalSaleId(
    @Parameter(
      description = "ID of the original sale"
    ) @PathVariable Integer originalSaleId
  ) {
    return saleReturnService.findByOriginalSaleId(originalSaleId);
  }

  /**
   * Deletes a sales return identified by its ID.
   *
   * <p>Upon successful deletion, product stock levels are adjusted to reflect that
   * the items are no longer considered returned (i.e., stock is decreased by the returned quantity).
   * This action is irreversible.</p>
   *
   * @param id The ID of the sales return to delete.
   * @see SaleReturnService#delete(Integer)
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
    summary = "Delete a sales return",
    description = "Deletes a sales return and reverts associated stock adjustments."
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "204",
        description = "Sales return deleted successfully"
      ),
      @ApiResponse(
        responseCode = "404",
        description = "Sales return not found"
      ),
    }
  )
  public void delete(
    @Parameter(
      description = "ID of the sales return to delete"
    ) @PathVariable Integer id
  ) {
    saleReturnService.delete(id);
  }
}
