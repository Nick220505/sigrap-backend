package com.sigrap.supplier;

import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing purchase order operations.
 * Handles business logic for creating, reading, updating, and deleting purchase orders.
 * Also manages order status transitions and relationships with suppliers and products.
 */
@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

  /**
   * Repository for database operations on purchase orders.
   */
  private final PurchaseOrderRepository purchaseOrderRepository;

  /**
   * Repository for database operations on purchase order items.
   */
  private final PurchaseOrderItemRepository purchaseOrderItemRepository;

  /**
   * Repository for accessing supplier data.
   */
  private final SupplierRepository supplierRepository;

  /**
   * Repository for accessing product data.
   */
  private final ProductRepository productRepository;

  /**
   * Mapper for converting between PurchaseOrder entities and DTOs.
   */
  private final PurchaseOrderMapper purchaseOrderMapper;

  /**
   * Mapper for converting between PurchaseOrderItem entities and DTOs.
   */
  private final PurchaseOrderItemMapper purchaseOrderItemMapper;

  /**
   * Retrieves all purchase orders from the database.
   *
   * @return List of all purchase orders mapped to PurchaseOrderInfo objects
   */
  @Transactional(readOnly = true)
  public List<PurchaseOrderInfo> findAll() {
    return purchaseOrderRepository
      .findAll()
      .stream()
      .map(purchaseOrderMapper::toInfo)
      .toList();
  }

  /**
   * Finds a purchase order by its ID.
   *
   * @param id The ID of the purchase order to find
   * @return The found purchase order mapped to PurchaseOrderInfo
   * @throws EntityNotFoundException if the purchase order is not found
   */
  @Transactional(readOnly = true)
  public PurchaseOrderInfo findById(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );
    return purchaseOrderMapper.toInfo(purchaseOrder);
  }

  /**
   * Finds all purchase orders for a specific supplier.
   *
   * @param supplierId The ID of the supplier
   * @return List of purchase orders for the supplier
   */
  @Transactional(readOnly = true)
  public List<PurchaseOrderInfo> findBySupplierId(Integer supplierId) {
    List<PurchaseOrder> purchaseOrders =
      purchaseOrderRepository.findBySupplier_Id(supplierId);
    return purchaseOrderMapper.toInfoList(purchaseOrders);
  }

  /**
   * Finds all purchase orders with a specific status.
   *
   * @param status The status to filter by
   * @return List of purchase orders with the specified status
   */
  @Transactional(readOnly = true)
  public List<PurchaseOrderInfo> findByStatus(PurchaseOrder.Status status) {
    List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByStatus(
      status
    );
    return purchaseOrderMapper.toInfoList(purchaseOrders);
  }

  /**
   * Creates a new purchase order.
   *
   * @param purchaseOrderData The data for creating the purchase order
   * @return The created purchase order mapped to PurchaseOrderInfo
   * @throws EntityNotFoundException if the specified supplier or any product is not found
   */
  @Transactional
  public PurchaseOrderInfo create(PurchaseOrderData purchaseOrderData) {
    PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(
      purchaseOrderData
    );

    Supplier supplier = supplierRepository
      .findById(purchaseOrderData.getSupplierId().longValue())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Supplier not found with id: " + purchaseOrderData.getSupplierId()
        )
      );
    purchaseOrder.setSupplier(supplier);

    String orderNumber = generateOrderNumber();
    purchaseOrder.setOrderNumber(orderNumber);

    PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);

    if (
      purchaseOrderData.getItems() != null &&
      !purchaseOrderData.getItems().isEmpty()
    ) {
      List<PurchaseOrderItem> items = new ArrayList<>();

      for (PurchaseOrderItemData itemData : purchaseOrderData.getItems()) {
        PurchaseOrderItem item = purchaseOrderItemMapper.toEntity(itemData);

        Product product = productRepository
          .findById(itemData.getProductId())
          .orElseThrow(() ->
            new EntityNotFoundException(
              "Product not found with id: " + itemData.getProductId()
            )
          );

        item.setProduct(product);
        item.setPurchaseOrder(savedOrder);
        item.setTotalPrice(
          product.getCostPrice().multiply(new BigDecimal(item.getQuantity()))
        );
        items.add(item);
      }

      purchaseOrderItemRepository.saveAll(items);
      savedOrder.setItems(items);
      calculateTotalAmount(savedOrder);
      purchaseOrderRepository.save(savedOrder);
    }

    return purchaseOrderMapper.toInfo(savedOrder);
  }

  /**
   * Updates an existing purchase order.
   *
   * @param id The ID of the purchase order to update
   * @param purchaseOrderData The new data for the purchase order
   * @return The updated purchase order mapped to PurchaseOrderInfo
   * @throws EntityNotFoundException if the purchase order, supplier, or any product is not found
   * @throws IllegalStateException if the order is not in DRAFT status
   */
  @Transactional
  public PurchaseOrderInfo update(
    Integer id,
    PurchaseOrderData purchaseOrderData
  ) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (purchaseOrder.getStatus() != PurchaseOrder.Status.DRAFT) {
      throw new IllegalStateException(
        "Only purchase orders in DRAFT status can be updated"
      );
    }

    if (purchaseOrderData.getSupplierId() != null) {
      Supplier supplier = supplierRepository
        .findById(purchaseOrderData.getSupplierId().longValue())
        .orElseThrow(() ->
          new EntityNotFoundException(
            "Supplier not found with id: " + purchaseOrderData.getSupplierId()
          )
        );
      purchaseOrder.setSupplier(supplier);
    }

    if (purchaseOrderData.getOrderDate() != null) {
      purchaseOrder.setOrderDate(purchaseOrderData.getOrderDate());
    }

    if (purchaseOrderData.getExpectedDeliveryDate() != null) {
      purchaseOrder.setExpectedDeliveryDate(
        purchaseOrderData.getExpectedDeliveryDate()
      );
    }

    if (
      purchaseOrderData.getItems() != null &&
      !purchaseOrderData.getItems().isEmpty()
    ) {
      purchaseOrderItemRepository.deleteAll(purchaseOrder.getItems());
      purchaseOrder.getItems().clear();

      List<PurchaseOrderItem> items = new ArrayList<>();

      for (PurchaseOrderItemData itemData : purchaseOrderData.getItems()) {
        PurchaseOrderItem item = purchaseOrderItemMapper.toEntity(itemData);

        Product product = productRepository
          .findById(itemData.getProductId())
          .orElseThrow(() ->
            new EntityNotFoundException(
              "Product not found with id: " + itemData.getProductId()
            )
          );

        item.setProduct(product);
        item.setPurchaseOrder(purchaseOrder);
        item.setTotalPrice(
          product.getCostPrice().multiply(new BigDecimal(item.getQuantity()))
        );
        items.add(item);
      }

      purchaseOrderItemRepository.saveAll(items);
      purchaseOrder.setItems(items);
    }

    calculateTotalAmount(purchaseOrder);
    PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
    return purchaseOrderMapper.toInfo(updatedOrder);
  }

  /**
   * Deletes a purchase order.
   *
   * @param id The ID of the purchase order to delete
   * @throws EntityNotFoundException if the purchase order is not found
   * @throws IllegalStateException if the order is not in DRAFT status
   */
  @Transactional
  public void delete(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (purchaseOrder.getStatus() != PurchaseOrder.Status.DRAFT) {
      throw new IllegalStateException(
        "Only purchase orders in DRAFT status can be deleted"
      );
    }

    purchaseOrderRepository.delete(purchaseOrder);
  }

  /**
   * Updates the status of a purchase order to SUBMITTED.
   *
   * @param id The ID of the purchase order to submit
   * @return The updated purchase order mapped to PurchaseOrderInfo
   * @throws EntityNotFoundException if the purchase order is not found
   * @throws IllegalStateException if the order is not in DRAFT status
   */
  @Transactional
  public PurchaseOrderInfo submitOrder(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (purchaseOrder.getStatus() != PurchaseOrder.Status.DRAFT) {
      throw new IllegalStateException(
        "Cannot submit order in " + purchaseOrder.getStatus() + " status"
      );
    }

    purchaseOrder.setStatus(PurchaseOrder.Status.SUBMITTED);

    PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
    return purchaseOrderMapper.toInfo(updatedOrder);
  }

  /**
   * Updates the status of a purchase order to CONFIRMED.
   *
   * @param id The ID of the purchase order to confirm
   * @return The updated purchase order mapped to PurchaseOrderInfo
   * @throws EntityNotFoundException if the purchase order is not found
   * @throws IllegalStateException if the order is not in SUBMITTED status
   */
  @Transactional
  public PurchaseOrderInfo confirmOrder(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (purchaseOrder.getStatus() != PurchaseOrder.Status.SUBMITTED) {
      throw new IllegalStateException(
        "Cannot confirm order in " + purchaseOrder.getStatus() + " status"
      );
    }

    purchaseOrder.setStatus(PurchaseOrder.Status.CONFIRMED);

    PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
    return purchaseOrderMapper.toInfo(updatedOrder);
  }

  /**
   * Updates the status of a purchase order to SHIPPED.
   *
   * @param id The ID of the purchase order to mark as shipped
   * @return The updated purchase order mapped to PurchaseOrderInfo
   * @throws EntityNotFoundException if the purchase order is not found
   * @throws IllegalStateException if the order is not in CONFIRMED status
   */
  @Transactional
  public PurchaseOrderInfo markAsShipped(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (
      purchaseOrder.getStatus() != PurchaseOrder.Status.CONFIRMED &&
      purchaseOrder.getStatus() != PurchaseOrder.Status.IN_PROCESS
    ) {
      throw new IllegalStateException(
        "Cannot mark as shipped order in " +
        purchaseOrder.getStatus() +
        " status"
      );
    }

    purchaseOrder.setStatus(PurchaseOrder.Status.SHIPPED);

    PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
    return purchaseOrderMapper.toInfo(updatedOrder);
  }

  /**
   * Updates the status of a purchase order to DELIVERED and sets the actual delivery date.
   *
   * @param id The ID of the purchase order to mark as delivered
   * @param actualDeliveryDate The actual delivery date
   * @return The updated purchase order mapped to PurchaseOrderInfo
   * @throws EntityNotFoundException if the purchase order is not found
   * @throws IllegalStateException if the order is not in SHIPPED status
   */
  @Transactional
  public PurchaseOrderInfo markAsDelivered(
    Integer id,
    LocalDate actualDeliveryDate
  ) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (purchaseOrder.getStatus() != PurchaseOrder.Status.SHIPPED) {
      throw new IllegalStateException(
        "Cannot mark as delivered order in " +
        purchaseOrder.getStatus() +
        " status"
      );
    }

    purchaseOrder.setStatus(PurchaseOrder.Status.DELIVERED);
    purchaseOrder.setActualDeliveryDate(actualDeliveryDate);

    PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
    return purchaseOrderMapper.toInfo(updatedOrder);
  }

  /**
   * Updates the status of a purchase order to CANCELLED.
   *
   * @param id The ID of the purchase order to cancel
   * @return The updated purchase order mapped to PurchaseOrderInfo
   * @throws EntityNotFoundException if the purchase order is not found
   * @throws IllegalStateException if the order is in DELIVERED or CANCELLED status
   */
  @Transactional
  public PurchaseOrderInfo cancelOrder(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (
      purchaseOrder.getStatus() == PurchaseOrder.Status.DELIVERED ||
      purchaseOrder.getStatus() == PurchaseOrder.Status.CANCELLED
    ) {
      throw new IllegalStateException(
        "Cannot cancel order in " + purchaseOrder.getStatus() + " status"
      );
    }

    purchaseOrder.setStatus(PurchaseOrder.Status.CANCELLED);

    PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
    return purchaseOrderMapper.toInfo(updatedOrder);
  }

  /**
   * Calculates the total amount of a purchase order based on its items.
   *
   * @param purchaseOrder The purchase order to calculate the total for
   */
  private void calculateTotalAmount(PurchaseOrder purchaseOrder) {
    BigDecimal total = purchaseOrder
      .getItems()
      .stream()
      .map(PurchaseOrderItem::getTotalPrice)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    purchaseOrder.setTotalAmount(total);
  }

  /**
   * Generates a unique order number.
   *
   * @return A unique order number
   */
  private String generateOrderNumber() {
    return "ORD-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
  }
}
