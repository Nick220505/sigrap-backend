package com.sigrap.notification;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a notification preference in the system.
 * Stores user preferences for different types of notifications.
 */
@Entity
@Table(name = "notification_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

  /**
   * Unique identifier for the notification preference.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * The ID of the user this preference belongs to.
   */
  @NotNull
  @Column(name = "user_id", nullable = false)
  private Long userId;

  /**
   * The type of notification (EMAIL, SMS, PUSH).
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;

  /**
   * The notification channel (IMMEDIATE, DAILY, WEEKLY).
   */
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationChannel channel;

  /**
   * Whether this notification type is enabled.
   */
  @NotNull
  @Column(nullable = false)
  private Boolean enabled;

  /**
   * Timestamp of when the preference was created.
   */
  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the preference was last updated.
   */
  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}

/**
 * Enum representing different types of notifications.
 */
enum NotificationType {
  EMAIL,
  SMS,
  PUSH,
}

/**
 * Enum representing different notification delivery channels.
 */
enum NotificationChannel {
  IMMEDIATE,
  DAILY,
  WEEKLY,
}
