package com.sigrap.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for notification preference information.
 * Used for returning notification preference data in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Notification preference information")
public class NotificationPreferenceInfo {

  /**
   * Unique identifier for the notification preference.
   */
  @Schema(description = "Notification preference ID", example = "1")
  private Integer id;

  /**
   * The ID of the user this preference belongs to.
   */
  @Schema(description = "User ID", example = "42")
  private Long userId;

  /**
   * The type of notification.
   */
  @Schema(description = "Notification type", example = "EMAIL")
  private NotificationType type;

  /**
   * The notification channel.
   */
  @Schema(description = "Notification channel", example = "IMMEDIATE")
  private NotificationChannel channel;

  /**
   * Whether this notification type is enabled.
   */
  @Schema(description = "Whether notifications are enabled", example = "true")
  private Boolean enabled;

  /**
   * Timestamp of when the preference was created.
   */
  @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
  private LocalDateTime createdAt;

  /**
   * Timestamp of when the preference was last updated.
   */
  @Schema(
    description = "Last update timestamp",
    example = "2024-01-20T14:45:00"
  )
  private LocalDateTime updatedAt;
}
