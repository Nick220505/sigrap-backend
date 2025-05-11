package com.sigrap.user;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between UserNotificationPreference entities and DTOs.
 * Handles the transformation of data between different formats.
 */
@Component
@RequiredArgsConstructor
public class UserNotificationPreferenceMapper {

  private final UserRepository userRepository;

  /**
   * Converts a UserNotificationPreference entity to a UserNotificationPreferenceInfo DTO.
   *
   * @param preference The UserNotificationPreference entity to convert
   * @return UserNotificationPreferenceInfo containing the preference data
   */
  public UserNotificationPreferenceInfo toInfo(
    UserNotificationPreference preference
  ) {
    if (preference == null) {
      return null;
    }

    return UserNotificationPreferenceInfo.builder()
      .id(preference.getId())
      .userId(preference.getUser().getId())
      .notificationType(preference.getNotificationType())
      .enabled(preference.getEnabled())
      .emailEnabled(preference.getEmailEnabled())
      .pushEnabled(preference.getPushEnabled())
      .build();
  }

  /**
   * Converts a list of UserNotificationPreference entities to a list of UserNotificationPreferenceInfo DTOs.
   *
   * @param preferences The list of UserNotificationPreference entities to convert
   * @return List of UserNotificationPreferenceInfo DTOs
   */
  public List<UserNotificationPreferenceInfo> toInfoList(
    List<UserNotificationPreference> preferences
  ) {
    if (preferences == null) {
      return List.of();
    }

    return preferences.stream().map(this::toInfo).collect(Collectors.toList());
  }

  /**
   * Converts a UserNotificationPreferenceData DTO to a new UserNotificationPreference entity.
   *
   * @param data The UserNotificationPreferenceData DTO to convert
   * @return UserNotificationPreference entity with data from the DTO
   */
  public UserNotificationPreference toEntity(
    UserNotificationPreferenceData data
  ) {
    if (data == null) {
      return null;
    }

    User user = userRepository
      .findById(data.getUserId())
      .orElseThrow(() ->
        new IllegalArgumentException("User not found: " + data.getUserId())
      );

    return UserNotificationPreference.builder()
      .user(user)
      .notificationType(data.getNotificationType())
      .enabled(data.getEnabled())
      .emailEnabled(data.getEmailEnabled())
      .pushEnabled(data.getPushEnabled())
      .build();
  }

  /**
   * Updates an existing UserNotificationPreference entity with data from a UserNotificationPreferenceData DTO.
   *
   * @param preference The UserNotificationPreference entity to update
   * @param data The UserNotificationPreferenceData DTO containing the new data
   */
  public void updateEntityFromData(
    UserNotificationPreference preference,
    UserNotificationPreferenceData data
  ) {
    if (preference == null || data == null) {
      return;
    }

    if (data.getEnabled() != null) {
      preference.setEnabled(data.getEnabled());
    }

    if (data.getEmailEnabled() != null) {
      preference.setEmailEnabled(data.getEmailEnabled());
    }

    if (data.getPushEnabled() != null) {
      preference.setPushEnabled(data.getPushEnabled());
    }
  }
}
