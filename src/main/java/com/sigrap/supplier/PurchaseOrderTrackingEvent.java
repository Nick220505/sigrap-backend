package com.sigrap.supplier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entity representing a single tracking event for a purchase order.
 * Stores details about status changes, location updates, and other relevant
 * information during the lifecycle of an order.
 */
@Entity
@Table(name = "purchase_order_tracking_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderTrackingEvent {

  /**
   * Unique identifier for the tracking event.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The purchase order this tracking event belongs to.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "purchase_order_id", nullable = false)
  private PurchaseOrder purchaseOrder;

  /**
   * Timestamp when the event occurred.
   * Automatically set to the creation time of this record.
   */
  @CreationTimestamp
  @Column(name = "event_timestamp", nullable = false, updatable = false)
  private LocalDateTime eventTimestamp;

  /**
   * The status or type of the event (e.g., "Order Placed", "Shipped", "In Transit").
   * Must not be blank.
   */
  @NotBlank(message = "Event status cannot be blank")
  @Size(max = 100, message = "Event status must be less than 100 characters")
  @Column(name = "status", nullable = false, length = 100)
  private String status;

  /**
   * Detailed description of the event.
   * Provides more context about what happened.
   */
  @Size(
    max = 500,
    message = "Event description must be less than 500 characters"
  )
  @Column(name = "description", length = 500)
  private String description;

  /**
   * Location where the event occurred, if applicable.
   * (e.g., "Warehouse A, Lima", "Customs Office, Callao").
   */
  @Size(max = 255, message = "Location must be less than 255 characters")
  @Column(name = "location", length = 255)
  private String location;

  /**
   * Additional notes or comments related to this tracking event.
   */
  @Size(max = 1000, message = "Notes must be less than 1000 characters")
  @Column(name = "notes", length = 1000)
  private String notes;
}
