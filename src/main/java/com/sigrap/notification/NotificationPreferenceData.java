package com.sigrap.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for creating and updating notification preferences.
 * Contains validated notification preference data for input operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for creating or updating a notification preference")
public class NotificationPreferenceData {

  /**
   * The ID of the user this preference belongs to.
   */
  @NotNull(message = "User ID cannot be null")
  @Schema(description = "User ID", example = "42")
  private Long userId;

  /**
   * The type of notification.
   */
  @NotNull(message = "Notification type cannot be null")
  @Schema(description = "Notification type", example = "EMAIL")
  private NotificationType type;

  /**
   * The notification channel.
   */
  @NotNull(message = "Notification channel cannot be null")
  @Schema(description = "Notification channel", example = "IMMEDIATE")
  private NotificationChannel channel;

  /**
   * Whether this notification type is enabled.
   */
  @NotNull(message = "Enabled flag cannot be null")
  @Schema(description = "Whether notifications are enabled", example = "true")
  private Boolean enabled;
}
