package com.sigrap.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user notification preference management.
 * Provides endpoints for retrieving and updating notification preferences.
 */
@RestController
@RequestMapping("/api/users/{userId}/notification-preferences")
@RequiredArgsConstructor
@Tag(
  name = "User Notification Preferences",
  description = "User notification preference management API"
)
public class UserNotificationPreferenceController {

  private final UserNotificationPreferenceService preferenceService;

  /**
   * Retrieves all notification preferences for a user.
   *
   * @param userId The ID of the user to retrieve preferences for
   * @return List of UserNotificationPreferenceInfo DTOs
   */
  @GetMapping
  @Operation(
    summary = "Get user notification preferences",
    description = "Retrieves a list of all notification preferences for a specific user"
  )
  public List<UserNotificationPreferenceInfo> getUserNotificationPreferences(
    @PathVariable Long userId
  ) {
    return preferenceService.findByUserId(userId);
  }

  /**
   * Updates a user's notification preference.
   *
   * @param userId The ID of the user
   * @param id The ID of the notification preference to update
   * @param preferenceData The new data for the preference
   * @return UserNotificationPreferenceInfo containing the updated preference
   */
  @PutMapping("/{id}")
  @Operation(
    summary = "Update notification preference",
    description = "Updates a specific notification preference for a user"
  )
  public UserNotificationPreferenceInfo updateUserNotificationPreference(
    @PathVariable Long userId,
    @PathVariable Long id,
    @Valid @RequestBody UserNotificationPreferenceData preferenceData
  ) {
    if (!userId.equals(preferenceData.getUserId())) {
      throw new IllegalArgumentException(
        "User ID in path must match user ID in request body"
      );
    }

    return preferenceService.update(id, preferenceData);
  }

  /**
   * Creates or updates a user's notification preference for a specific notification type.
   *
   * @param userId The ID of the user
   * @param preferenceData The data for the preference
   * @return UserNotificationPreferenceInfo containing the created/updated preference
   */
  @PutMapping
  @Operation(
    summary = "Create or update notification preference",
    description = "Creates a new notification preference or updates an existing one for a user"
  )
  public UserNotificationPreferenceInfo createOrUpdateUserNotificationPreference(
    @PathVariable Long userId,
    @Valid @RequestBody UserNotificationPreferenceData preferenceData
  ) {
    if (!userId.equals(preferenceData.getUserId())) {
      throw new IllegalArgumentException(
        "User ID in path must match user ID in request body"
      );
    }

    return preferenceService.createOrUpdate(preferenceData);
  }

  /**
   * Retrieves the default notification preferences configuration.
   *
   * @return List of default UserNotificationPreferenceInfo DTOs
   */
  @GetMapping("/defaults")
  @Operation(
    summary = "Get default notification preferences",
    description = "Retrieves the default notification preferences configuration"
  )
  public List<
    UserNotificationPreferenceInfo
  > getDefaultNotificationPreferences() {
    return preferenceService.getDefaultPreferences();
  }
}
