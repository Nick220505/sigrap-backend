package com.sigrap.user;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a user's notification preferences.
 * Controls how and when users receive notifications from the system.
 */
@Entity
@Table(name = "user_notification_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationPreference {

  /**
   * Unique identifier for the notification preference.
   * Auto-generated using identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The user whom these notification preferences belong to.
   * Many-to-one relationship with User.
   */
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /**
   * The type of notification this preference applies to.
   */
  @Enumerated(EnumType.STRING)
  @NotNull
  private NotificationType notificationType;

  /**
   * Whether notifications of this type are enabled at all.
   */
  @Builder.Default
  private Boolean enabled = true;

  /**
   * Whether email delivery is enabled for this notification type.
   */
  @Builder.Default
  private Boolean emailEnabled = true;

  /**
   * Whether push delivery is enabled for this notification type.
   */
  @Builder.Default
  private Boolean pushEnabled = false;

  /**
   * Enum representing the different types of notifications in the system.
   */
  public enum NotificationType {
    /**
     * Notifications about system events and announcements.
     */
    SYSTEM,

    /**
     * Notifications about order status changes.
     */
    ORDER,

    /**
     * Notifications about inventory changes (low stock, etc).
     */
    INVENTORY,

    /**
     * Notifications about security events (password changes, etc).
     */
    SECURITY,

    /**
     * Notifications about new features or updates.
     */
    MARKETING,
  }
}
