package com.sigrap.supplier;

import io.swagger.v3.oas.annotations.Operation;
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
 * REST Controller for purchase order operations.
 * Provides endpoints for managing purchase orders to suppliers.
 */
@RestController
@RequestMapping("/api/purchase-orders")
@Tag(
  name = "Purchase Orders",
  description = "API for managing purchase orders to suppliers"
)
@RequiredArgsConstructor
public class PurchaseOrderController {

  private final PurchaseOrderService purchaseOrderService;

  /**
   * Gets all purchase orders.
   *
   * @return List of purchase orders
   */
  @GetMapping
  @Operation(summary = "Get all purchase orders")
  public List<PurchaseOrderInfo> findAll() {
    return purchaseOrderService.findAll();
  }

  /**
   * Gets a specific purchase order by ID.
   *
   * @param id The ID of the purchase order to retrieve
   * @return The purchase order info
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get a purchase order by ID")
  public PurchaseOrderInfo findById(@PathVariable Integer id) {
    return purchaseOrderService.findById(id);
  }

  /**
   * Gets all purchase orders for a specific supplier.
   *
   * @param supplierId The ID of the supplier
   * @return List of purchase orders for the supplier
   */
  @GetMapping("/by-supplier/{supplierId}")
  @Operation(summary = "Get all purchase orders for a specific supplier")
  public List<PurchaseOrderInfo> findBySupplierId(
    @PathVariable Integer supplierId
  ) {
    return purchaseOrderService.findBySupplierId(supplierId);
  }

  /**
   * Gets all purchase orders with a specific status.
   *
   * @param status The status to filter by
   * @return List of purchase orders with the specified status
   */
  @GetMapping("/by-status/{status}")
  @Operation(summary = "Get all purchase orders with a specific status")
  public List<PurchaseOrderInfo> findByStatus(
    @PathVariable PurchaseOrderStatus status
  ) {
    return purchaseOrderService.findByStatus(status);
  }

  /**
   * Creates a new purchase order.
   *
   * @param purchaseOrderData The data for creating the purchase order
   * @return The created purchase order
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a new purchase order")
  public PurchaseOrderInfo create(
    @Valid @RequestBody PurchaseOrderData purchaseOrderData
  ) {
    return purchaseOrderService.create(purchaseOrderData);
  }

  /**
   * Updates an existing purchase order.
   *
   * @param id The ID of the purchase order to update
   * @param purchaseOrderData The new data for the purchase order
   * @return The updated purchase order
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update an existing purchase order")
  public PurchaseOrderInfo update(
    @PathVariable Integer id,
    @Valid @RequestBody PurchaseOrderData purchaseOrderData
  ) {
    return purchaseOrderService.update(id, purchaseOrderData);
  }

  /**
   * Deletes a purchase order.
   *
   * @param id The ID of the purchase order to delete
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a purchase order")
  public void delete(@PathVariable Integer id) {
    purchaseOrderService.delete(id);
  }

  /**
   * Updates the status of a purchase order to SUBMITTED.
   *
   * @param id The ID of the purchase order to submit
   * @return The updated purchase order
   */
  @PatchMapping("/{id}/submit")
  @Operation(summary = "Submit a purchase order")
  public PurchaseOrderInfo submitOrder(@PathVariable Integer id) {
    return purchaseOrderService.submitOrder(id);
  }

  /**
   * Updates the status of a purchase order to CONFIRMED.
   *
   * @param id The ID of the purchase order to confirm
   * @return The updated purchase order
   */
  @PatchMapping("/{id}/confirm")
  @Operation(summary = "Confirm a purchase order")
  public PurchaseOrderInfo confirmOrder(@PathVariable Integer id) {
    return purchaseOrderService.confirmOrder(id);
  }

  /**
   * Updates the status of a purchase order to SHIPPED.
   *
   * @param id The ID of the purchase order to mark as shipped
   * @return The updated purchase order
   */
  @PatchMapping("/{id}/ship")
  @Operation(summary = "Mark a purchase order as shipped")
  public PurchaseOrderInfo markAsShipped(@PathVariable Integer id) {
    return purchaseOrderService.markAsShipped(id);
  }

  /**
   * Updates the status of a purchase order to DELIVERED.
   *
   * @param id The ID of the purchase order to mark as delivered
   * @return The updated purchase order
   */
  @PatchMapping("/{id}/deliver")
  @Operation(summary = "Mark a purchase order as delivered")
  public PurchaseOrderInfo markAsDelivered(@PathVariable Integer id) {
    return purchaseOrderService.markAsDelivered(id);
  }

  /**
   * Updates the status of a purchase order to CANCELLED.
   *
   * @param id The ID of the purchase order to cancel
   * @return The updated purchase order
   */
  @PatchMapping("/{id}/cancel")
  @Operation(summary = "Cancel a purchase order")
  public PurchaseOrderInfo cancelOrder(@PathVariable Integer id) {
    return purchaseOrderService.cancelOrder(id);
  }

  /**
   * Updates the status of a purchase order to PAID.
   *
   * @param id The ID of the purchase order to mark as paid
   * @return The updated purchase order
   */
  @PatchMapping("/{id}/pay")
  @Operation(summary = "Mark a purchase order as paid")
  public PurchaseOrderInfo markAsPaid(@PathVariable Integer id) {
    return purchaseOrderService.markAsPaid(id);
  }
}
