package com.sigrap.user;

import com.sigrap.user.UserNotificationPreference.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for user notification preference information.
 * Contains notification preference data returned in API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User notification preference information")
public class UserNotificationPreferenceInfo {

  /**
   * Unique identifier for the notification preference.
   */
  @Schema(description = "Notification preference ID", example = "1")
  private Long id;

  /**
   * The ID of the user these preferences belong to.
   */
  @Schema(description = "User ID", example = "42")
  private Long userId;

  /**
   * The type of notification this preference applies to.
   */
  @Schema(description = "Notification type", example = "SYSTEM")
  private NotificationType notificationType;

  /**
   * Whether notifications of this type are enabled at all.
   */
  @Schema(description = "Whether notifications are enabled", example = "true")
  private Boolean enabled;

  /**
   * Whether email delivery is enabled for this notification type.
   */
  @Schema(
    description = "Whether email notifications are enabled",
    example = "true"
  )
  private Boolean emailEnabled;

  /**
   * Whether push delivery is enabled for this notification type.
   */
  @Schema(
    description = "Whether push notifications are enabled",
    example = "false"
  )
  private Boolean pushEnabled;
}
