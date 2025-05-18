package com.sigrap.supplier;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for PurchaseOrder entity operations.
 * Provides CRUD operations and custom queries for purchase orders.
 */
@Repository
public interface PurchaseOrderRepository
  extends JpaRepository<PurchaseOrder, Integer> {
  /**
   * Find all purchase orders for a specific supplier.
   *
   * @param supplierId the ID of the supplier
   * @return list of purchase orders
   */
  List<PurchaseOrder> findBySupplier_Id(Integer supplierId);

  /**
   * Find all purchase orders with a specific status.
   *
   * @param status the status to filter by
   * @return list of purchase orders
   */
  List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

  /**
   * Find all purchase orders created between the specified dates.
   *
   * @param start the start date (inclusive)
   * @param end the end date (inclusive)
   * @return list of purchase orders
   */
  List<PurchaseOrder> findByCreatedAtBetween(LocalDate start, LocalDate end);

  /**
   * Find all purchase orders with an expected delivery date between the specified dates.
   *
   * @param start the start date (inclusive)
   * @param end the end date (inclusive)
   * @return list of purchase orders
   */
  List<PurchaseOrder> findByDeliveryDateBetween(LocalDate start, LocalDate end);
}
