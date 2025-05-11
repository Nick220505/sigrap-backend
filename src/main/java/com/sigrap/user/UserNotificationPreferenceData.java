package com.sigrap.user;

import com.sigrap.user.UserNotificationPreference.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating or updating user notification preferences.
 * Contains the data needed for notification preference creation and modification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for creating or updating a notification preference")
public class UserNotificationPreferenceData {

  /**
   * The ID of the user these preferences belong to.
   */
  @NotNull(message = "User ID is required")
  @Schema(description = "ID of the user", example = "42")
  private Long userId;

  /**
   * The type of notification this preference applies to.
   */
  @NotNull(message = "Notification type is required")
  @Schema(description = "Type of notification", example = "SYSTEM")
  private NotificationType notificationType;

  /**
   * Whether notifications of this type are enabled at all.
   */
  @Schema(
    description = "Whether notifications are enabled",
    example = "true",
    defaultValue = "true"
  )
  @Builder.Default
  private Boolean enabled = true;

  /**
   * Whether email delivery is enabled for this notification type.
   */
  @Schema(
    description = "Whether email notifications are enabled",
    example = "true",
    defaultValue = "true"
  )
  @Builder.Default
  private Boolean emailEnabled = true;

  /**
   * Whether push delivery is enabled for this notification type.
   */
  @Schema(
    description = "Whether push notifications are enabled",
    example = "false",
    defaultValue = "false"
  )
  @Builder.Default
  private Boolean pushEnabled = false;
}
