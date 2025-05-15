package com.sigrap.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing payments.
 *
 * <p>This controller provides API endpoints for performing CRUD operations on payments,
 * as well as operations to find payments by specific criteria and mark them as completed.
 * It handles HTTP requests and delegates business logic to the {@link PaymentService}.</p>
 *
 * <p>Endpoints include:
 * <ul>
 *   <li>{@code GET /api/payments}: Retrieves all payments.</li>
 *   <li>{@code POST /api/payments}: Creates a new payment.</li>
 *   <li>{@code GET /api/payments/{id}}: Retrieves a specific payment by its ID.</li>
 *   <li>{@code PUT /api/payments/{id}}: Updates an existing payment.</li>
 *   <li>{@code DELETE /api/payments/{id}}: Deletes a payment.</li>
 *   <li>{@code GET /api/payments/supplier/{supplierId}}: Finds payments by supplier ID.</li>
 *   <li>{@code GET /api/payments/status/{status}}: Finds payments by status.</li>
 *   <li>{@code PATCH /api/payments/{id}/complete}: Marks a payment as completed.</li>
 * </ul>
 * </p>
 *
 * @see PaymentService
 * @see PaymentInfo
 * @see PaymentData
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "API for managing supplier payments")
public class PaymentController {

  private final PaymentService paymentService;

  /**
   * Retrieves a list of all payments.
   *
   * @return A list of {@link PaymentInfo} objects.
   */
  @Operation(
    summary = "Get all payments",
    description = "Retrieves a list of all supplier payments."
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved list of payments",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = PaymentInfo.class)
    )
  )
  @GetMapping
  public List<PaymentInfo> getAll() {
    return paymentService.findAll();
  }

  /**
   * Retrieves a specific payment by its ID.
   *
   * @param id The ID of the payment.
   * @return The {@link PaymentInfo} if found, otherwise a 404 Not Found response.
   */
  @Operation(
    summary = "Get payment by ID",
    description = "Retrieves a specific payment by its ID."
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved payment",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = PaymentInfo.class)
    )
  )
  @ApiResponse(responseCode = "404", description = "Payment not found")
  @GetMapping("/{id}")
  public PaymentInfo getById(
    @Parameter(
      description = "ID of the payment to be retrieved",
      required = true
    ) @PathVariable Integer id
  ) {
    return paymentService.findById(id);
  }

  /**
   * Creates a new payment.
   *
   * @param paymentData The data for the new payment.
   * @return The created {@link PaymentInfo} with HTTP status 201 (Created).
   */
  @Operation(
    summary = "Create a new payment",
    description = "Creates a new supplier payment."
  )
  @ApiResponse(
    responseCode = "201",
    description = "Payment created successfully",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = PaymentInfo.class)
    )
  )
  @ApiResponse(responseCode = "400", description = "Invalid input data")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PaymentInfo create(
    @Parameter(
      description = "Payment data for the new payment",
      required = true,
      content = @Content(schema = @Schema(implementation = PaymentData.class))
    ) @Valid @RequestBody PaymentData paymentData
  ) {
    return paymentService.create(paymentData);
  }

  /**
   * Updates an existing payment.
   *
   * @param id The ID of the payment to update.
   * @param paymentData The updated payment data.
   * @return The updated {@link PaymentInfo}.
   */
  @Operation(
    summary = "Update an existing payment",
    description = "Updates details of an existing supplier payment."
  )
  @ApiResponse(
    responseCode = "200",
    description = "Payment updated successfully",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = PaymentInfo.class)
    )
  )
  @ApiResponse(responseCode = "404", description = "Payment not found")
  @ApiResponse(responseCode = "400", description = "Invalid input data")
  @PutMapping("/{id}")
  public PaymentInfo update(
    @Parameter(
      description = "ID of the payment to be updated",
      required = true
    ) @PathVariable Integer id,
    @Parameter(
      description = "Updated payment data",
      required = true,
      content = @Content(schema = @Schema(implementation = PaymentData.class))
    ) @Valid @RequestBody PaymentData paymentData
  ) {
    return paymentService.update(id, paymentData);
  }

  /**
   * Deletes a payment by its ID.
   *
   * @param id The ID of the payment to delete.
   * @return HTTP status 204 (No Content) if successful.
   */
  @Operation(
    summary = "Delete a payment",
    description = "Deletes a supplier payment by its ID."
  )
  @ApiResponse(
    responseCode = "204",
    description = "Payment deleted successfully"
  )
  @ApiResponse(responseCode = "404", description = "Payment not found")
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
    @Parameter(
      description = "ID of the payment to be deleted",
      required = true
    ) @PathVariable Integer id
  ) {
    paymentService.delete(id);
  }

  /**
   * Deletes multiple payments by their IDs.
   *
   * @param ids A list of IDs of the payments to delete.
   */
  @Operation(
    summary = "Delete multiple payments",
    description = "Deletes multiple supplier payments by their IDs."
  )
  @ApiResponse(
    responseCode = "204",
    description = "Payments deleted successfully"
  )
  @ApiResponse(
    responseCode = "400",
    description = "Invalid input data (e.g., empty list of IDs)"
  )
  @DeleteMapping("/delete-many")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAllById(
    @Parameter(
      description = "List of IDs of the payments to be deleted",
      required = true
    ) @RequestBody List<Integer> ids
  ) {
    paymentService.deleteAllById(ids);
  }

  /**
   * Finds payments associated with a specific supplier ID.
   *
   * @param supplierId The ID of the supplier.
   * @return A list of {@link PaymentInfo} objects for the given supplier.
   */
  @Operation(
    summary = "Get payments by supplier ID",
    description = "Retrieves all payments for a specific supplier."
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved payments for supplier",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = PaymentInfo.class)
    )
  )
  @GetMapping("/supplier/{supplierId}")
  public List<PaymentInfo> getBySupplier(
    @Parameter(
      description = "ID of the supplier whose payments are to be retrieved",
      required = true
    ) @PathVariable Long supplierId
  ) {
    return paymentService.findBySupplier(supplierId);
  }

  /**
   * Finds payments by their status.
   *
   * @param status The payment status to filter by.
   * @return A list of {@link PaymentInfo} objects with the specified status.
   */
  @Operation(
    summary = "Get payments by status",
    description = "Retrieves payments filtered by their status."
  )
  @ApiResponse(
    responseCode = "200",
    description = "Successfully retrieved payments by status",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = PaymentInfo.class)
    )
  )
  @GetMapping("/status/{status}")
  public List<PaymentInfo> getByStatus(
    @Parameter(
      description = "Payment status to filter by (e.g., PENDING, COMPLETED)",
      required = true,
      schema = @Schema(implementation = PaymentStatus.class)
    ) @PathVariable PaymentStatus status
  ) {
    return paymentService.findByStatus(status);
  }

  /**
   * Marks a payment as completed.
   *
   * @param id The ID of the payment to mark as completed.
   * @return The updated {@link PaymentInfo}.
   */
  @Operation(
    summary = "Mark payment as completed",
    description = "Updates the status of a payment to COMPLETED."
  )
  @ApiResponse(
    responseCode = "200",
    description = "Payment marked as completed successfully",
    content = @Content(
      mediaType = "application/json",
      schema = @Schema(implementation = PaymentInfo.class)
    )
  )
  @ApiResponse(responseCode = "404", description = "Payment not found")
  @PatchMapping("/{id}/complete")
  public PaymentInfo markAsCompleted(
    @Parameter(
      description = "ID of the payment to be marked as completed",
      required = true
    ) @PathVariable Integer id
  ) {
    return paymentService.markAsCompleted(id);
  }
}
