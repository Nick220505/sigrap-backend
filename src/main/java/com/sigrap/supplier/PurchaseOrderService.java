package com.sigrap.supplier;

import com.sigrap.audit.Auditable;
import com.sigrap.product.Product;
import com.sigrap.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
  public List<PurchaseOrderInfo> findByStatus(PurchaseOrderStatus status) {
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
  @Auditable(action = "CREAR", entity = "ORDEN_COMPRA", captureDetails = true)
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
   * @param purchaseOrderData The data for updating the purchase order
   * @return The updated purchase order mapped to PurchaseOrderInfo
   * @throws EntityNotFoundException if the purchase order is not found
   * @throws IllegalStateException if the purchase order is not in DRAFT status
   */
  @Transactional
  @Auditable(
    action = "ACTUALIZAR",
    entity = "ORDEN_COMPRA",
    entityIdParam = "id",
    captureDetails = true
  )
  public PurchaseOrderInfo update(
    Integer id,
    PurchaseOrderData purchaseOrderData
  ) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {
      throw new IllegalStateException(
        "Only purchase orders in DRAFT status can be updated"
      );
    }

    purchaseOrderMapper.updateEntityFromData(purchaseOrderData, purchaseOrder);

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

    if (purchaseOrderData.getItems() != null) {
      List<PurchaseOrderItem> currentItems = new ArrayList<>(
        purchaseOrder.getItems()
      );

      for (PurchaseOrderItem existingItem : currentItems) {
        boolean found = false;
        for (PurchaseOrderItemData itemData : purchaseOrderData.getItems()) {
          if (
            itemData.getId() != null &&
            itemData.getId().equals(existingItem.getId())
          ) {
            found = true;
            break;
          }
        }

        if (!found) {
          purchaseOrder.removeItem(existingItem);
        }
      }

      for (PurchaseOrderItemData itemData : purchaseOrderData.getItems()) {
        Product product = productRepository
          .findById(itemData.getProductId())
          .orElseThrow(() ->
            new EntityNotFoundException(
              "Product not found with id: " + itemData.getProductId()
            )
          );

        if (itemData.getId() != null) {
          PurchaseOrderItem existingItem = purchaseOrder
            .getItems()
            .stream()
            .filter(item -> item.getId().equals(itemData.getId()))
            .findFirst()
            .orElse(null);

          if (existingItem != null) {
            existingItem.setProduct(product);
            existingItem.setQuantity(itemData.getQuantity());
            existingItem.setUnitPrice(itemData.getUnitPrice());
            existingItem.calculateTotalPrice();
          }
        } else {
          PurchaseOrderItem newItem = purchaseOrderItemMapper.toEntity(
            itemData
          );
          newItem.setProduct(product);
          newItem.setUnitPrice(itemData.getUnitPrice());
          newItem.calculateTotalPrice();
          purchaseOrder.addItem(newItem);
        }
      }
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
  @Auditable(action = "ELIMINAR", entity = "ORDEN_COMPRA", entityIdParam = "id")
  public void delete(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {
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
  @Auditable(action = "ENVIAR", entity = "ORDEN_COMPRA", entityIdParam = "id")
  public PurchaseOrderInfo submitOrder(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {
      throw new IllegalStateException(
        "Only purchase orders in DRAFT status can be submitted"
      );
    }

    purchaseOrder.setStatus(PurchaseOrderStatus.SUBMITTED);

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
  @Auditable(
    action = "CONFIRMAR",
    entity = "ORDEN_COMPRA",
    entityIdParam = "id"
  )
  public PurchaseOrderInfo confirmOrder(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (purchaseOrder.getStatus() != PurchaseOrderStatus.SUBMITTED) {
      throw new IllegalStateException(
        "Cannot confirm order in " + purchaseOrder.getStatus() + " status"
      );
    }

    purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);

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
  @Auditable(
    action = "MARCAR_ENVIADO",
    entity = "ORDEN_COMPRA",
    entityIdParam = "id"
  )
  public PurchaseOrderInfo markAsShipped(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (
      purchaseOrder.getStatus() != PurchaseOrderStatus.CONFIRMED &&
      purchaseOrder.getStatus() != PurchaseOrderStatus.IN_PROCESS
    ) {
      throw new IllegalStateException(
        "Cannot mark as shipped order in " +
        purchaseOrder.getStatus() +
        " status"
      );
    }

    purchaseOrder.setStatus(PurchaseOrderStatus.SHIPPED);

    PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
    return purchaseOrderMapper.toInfo(updatedOrder);
  }

  /**
   * Updates the status of a purchase order to DELIVERED.
   *
   * @param id The ID of the purchase order to mark as delivered
   * @return The updated purchase order mapped to PurchaseOrderInfo
   * @throws EntityNotFoundException if the purchase order is not found
   * @throws IllegalStateException if the order is not in SHIPPED status
   */
  @Transactional
  @Auditable(
    action = "MARCAR_ENTREGADO",
    entity = "ORDEN_COMPRA",
    entityIdParam = "id"
  )
  public PurchaseOrderInfo markAsDelivered(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (purchaseOrder.getStatus() != PurchaseOrderStatus.SHIPPED) {
      throw new IllegalStateException(
        "Cannot mark as delivered order in " +
        purchaseOrder.getStatus() +
        " status"
      );
    }

    purchaseOrder.setStatus(PurchaseOrderStatus.DELIVERED);

    PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
    return purchaseOrderMapper.toInfo(updatedOrder);
  }

  /**
   * Updates the status of a purchase order to CANCELLED.
   *
   * @param id The ID of the purchase order to cancel
   * @return The updated purchase order mapped to PurchaseOrderInfo
   * @throws EntityNotFoundException if the purchase order is not found
   * @throws IllegalStateException if the order is in DELIVERED status
   */
  @Transactional
  @Auditable(action = "CANCELAR", entity = "ORDEN_COMPRA", entityIdParam = "id")
  public PurchaseOrderInfo cancelOrder(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (
      purchaseOrder.getStatus() == PurchaseOrderStatus.DELIVERED ||
      purchaseOrder.getStatus() == PurchaseOrderStatus.CANCELLED
    ) {
      throw new IllegalStateException(
        "Cannot cancel order in " + purchaseOrder.getStatus() + " status"
      );
    }

    purchaseOrder.setStatus(PurchaseOrderStatus.CANCELLED);

    PurchaseOrder updatedOrder = purchaseOrderRepository.save(purchaseOrder);
    return purchaseOrderMapper.toInfo(updatedOrder);
  }

  /**
   * Updates the status of a purchase order to PAID.
   *
   * @param id The ID of the purchase order to mark as paid
   * @return The updated purchase order mapped to PurchaseOrderInfo
   * @throws EntityNotFoundException if the purchase order is not found
   * @throws IllegalStateException if the order is not in DELIVERED status
   */
  @Transactional
  @Auditable(
    action = "MARCAR_PAGADO",
    entity = "ORDEN_COMPRA",
    entityIdParam = "id"
  )
  public PurchaseOrderInfo markAsPaid(Integer id) {
    PurchaseOrder purchaseOrder = purchaseOrderRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Purchase order not found with id: " + id)
      );

    if (purchaseOrder.getStatus() != PurchaseOrderStatus.DELIVERED) {
      throw new IllegalStateException(
        "Cannot mark as paid order in " + purchaseOrder.getStatus() + " status"
      );
    }

    purchaseOrder.setStatus(PurchaseOrderStatus.PAID);

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
}
