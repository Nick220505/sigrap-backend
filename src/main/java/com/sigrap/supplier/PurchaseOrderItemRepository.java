package com.sigrap.supplier;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for PurchaseOrderItem entity operations.
 * Provides CRUD operations and custom queries for purchase order items.
 */
@Repository
public interface PurchaseOrderItemRepository
  extends JpaRepository<PurchaseOrderItem, Integer> {
  /**
   * Find all items for a specific purchase order.
   *
   * @param purchaseOrderId the ID of the purchase order
   * @return list of purchase order items
   */
  List<PurchaseOrderItem> findByPurchaseOrder_Id(Integer purchaseOrderId);

  /**
   * Find all items for a specific product.
   *
   * @param productId the ID of the product
   * @return list of purchase order items
   */
  List<PurchaseOrderItem> findByProduct_Id(Integer productId);
}
