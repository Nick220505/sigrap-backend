package com.sigrap.supplier;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for PurchaseOrderTrackingEvent entity operations.
 * Provides CRUD operations and custom queries for purchase order tracking events.
 */
@Repository
public interface PurchaseOrderTrackingEventRepository
  extends JpaRepository<PurchaseOrderTrackingEvent, Integer> {
  /**
   * Finds all tracking events for a specific purchase order, ordered by the event timestamp ascending.
   *
   * @param purchaseOrderId The ID of the purchase order.
   * @return A list of tracking events for the given order, sorted by time.
   */
  List<
    PurchaseOrderTrackingEvent
  > findByPurchaseOrder_IdOrderByEventTimestampAsc(Integer purchaseOrderId);
}
