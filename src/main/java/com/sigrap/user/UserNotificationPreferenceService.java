package com.sigrap.user;

import com.sigrap.user.UserNotificationPreference.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user notification preference management.
 * Provides methods for retrieving and updating notification preferences.
 */
@Service
@RequiredArgsConstructor
public class UserNotificationPreferenceService {

  private final UserNotificationPreferenceRepository preferenceRepository;
  private final UserRepository userRepository;
  private final UserNotificationPreferenceMapper preferenceMapper;

  /**
   * Retrieves all notification preferences for a user.
   *
   * @param userId The ID of the user to retrieve preferences for
   * @return List of UserNotificationPreferenceInfo DTOs
   * @throws EntityNotFoundException if the user is not found
   */
  @Transactional(readOnly = true)
  public List<UserNotificationPreferenceInfo> findByUserId(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new EntityNotFoundException("User not found: " + userId);
    }

    List<UserNotificationPreference> preferences =
      preferenceRepository.findByUserId(userId);

    if (preferences.isEmpty()) {
      preferences = createDefaultPreferences(userId);
    }

    return preferenceMapper.toInfoList(preferences);
  }

  /**
   * Updates a notification preference.
   *
   * @param id The ID of the preference to update
   * @param preferenceData The new data for the preference
   * @return UserNotificationPreferenceInfo containing the updated preference
   * @throws EntityNotFoundException if the preference is not found
   */
  @Transactional
  public UserNotificationPreferenceInfo update(
    Long id,
    UserNotificationPreferenceData preferenceData
  ) {
    UserNotificationPreference preference = preferenceRepository
      .findById(id)
      .orElseThrow(() ->
        new EntityNotFoundException("Notification preference not found: " + id)
      );

    preferenceMapper.updateEntityFromData(preference, preferenceData);
    UserNotificationPreference updatedPreference = preferenceRepository.save(
      preference
    );

    return preferenceMapper.toInfo(updatedPreference);
  }

  /**
   * Creates or updates a notification preference for a user and notification type.
   *
   * @param preferenceData The data for the preference
   * @return UserNotificationPreferenceInfo containing the created/updated preference
   * @throws EntityNotFoundException if the user is not found
   */
  @Transactional
  public UserNotificationPreferenceInfo createOrUpdate(
    UserNotificationPreferenceData preferenceData
  ) {
    if (!userRepository.existsById(preferenceData.getUserId())) {
      throw new EntityNotFoundException(
        "User not found: " + preferenceData.getUserId()
      );
    }

    Optional<UserNotificationPreference> existingPreference =
      preferenceRepository.findByUserIdAndNotificationType(
        preferenceData.getUserId(),
        preferenceData.getNotificationType()
      );

    if (existingPreference.isPresent()) {
      UserNotificationPreference preference = existingPreference.get();
      preferenceMapper.updateEntityFromData(preference, preferenceData);
      UserNotificationPreference updatedPreference = preferenceRepository.save(
        preference
      );
      return preferenceMapper.toInfo(updatedPreference);
    } else {
      UserNotificationPreference newPreference = preferenceMapper.toEntity(
        preferenceData
      );
      UserNotificationPreference savedPreference = preferenceRepository.save(
        newPreference
      );
      return preferenceMapper.toInfo(savedPreference);
    }
  }

  /**
   * Creates default notification preferences for a user.
   *
   * @param userId The ID of the user to create preferences for
   * @return List of created UserNotificationPreference entities
   * @throws EntityNotFoundException if the user is not found
   */
  @Transactional
  public List<UserNotificationPreference> createDefaultPreferences(
    Long userId
  ) {
    User user = userRepository
      .findById(userId)
      .orElseThrow(() ->
        new EntityNotFoundException("User not found: " + userId)
      );

    return Arrays.stream(NotificationType.values())
      .map(type -> {
        UserNotificationPreference preference =
          UserNotificationPreference.builder()
            .user(user)
            .notificationType(type)
            .enabled(true)
            .emailEnabled(true)
            .pushEnabled(type == NotificationType.SECURITY)
            .build();

        return preferenceRepository.save(preference);
      })
      .toList();
  }

  /**
   * Gets the default notification preferences configuration.
   *
   * @return List of default notification preferences
   */
  @Transactional(readOnly = true)
  public List<UserNotificationPreferenceInfo> getDefaultPreferences() {
    return Arrays.stream(NotificationType.values())
      .map(type -> {
        boolean pushEnabled = type == NotificationType.SECURITY;

        return UserNotificationPreferenceInfo.builder()
          .notificationType(type)
          .enabled(true)
          .emailEnabled(true)
          .pushEnabled(pushEnabled)
          .build();
      })
      .toList();
  }
}
