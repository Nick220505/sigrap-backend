package com.sigrap.notification;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing notification preference operations.
 * Handles business logic for creating, reading, updating, and deleting notification preferences.
 */
@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

  private final NotificationPreferenceRepository notificationPreferenceRepository;
  private final NotificationPreferenceMapper notificationPreferenceMapper;

  /**
   * Retrieves all notification preferences.
   *
   * @return List of all notification preferences
   */
  @Transactional(readOnly = true)
  public List<NotificationPreferenceInfo> findAll() {
    return notificationPreferenceRepository
      .findAll()
      .stream()
      .map(notificationPreferenceMapper::toInfo)
      .toList();
  }

  /**
   * Finds a notification preference by its ID.
   *
   * @param id The ID of the notification preference to find
   * @return The found notification preference
   * @throws EntityNotFoundException if the notification preference is not found
   */
  @Transactional(readOnly = true)
  public NotificationPreferenceInfo findById(Integer id) {
    NotificationPreference preference = notificationPreferenceRepository
      .findById(id)
      .orElseThrow(EntityNotFoundException::new);
    return notificationPreferenceMapper.toInfo(preference);
  }

  /**
   * Finds all notification preferences for a specific user.
   *
   * @param userId The ID of the user to find preferences for
   * @return List of notification preferences for the user
   */
  @Transactional(readOnly = true)
  public List<NotificationPreferenceInfo> findByUserId(Long userId) {
    return notificationPreferenceRepository
      .findByUserIdOrderByTypeAsc(userId)
      .stream()
      .map(notificationPreferenceMapper::toInfo)
      .toList();
  }

  /**
   * Creates a new notification preference.
   *
   * @param preferenceData The data for creating the notification preference
   * @return The created notification preference
   */
  @Transactional
  public NotificationPreferenceInfo create(
    NotificationPreferenceData preferenceData
  ) {
    NotificationPreference existingPreference =
      notificationPreferenceRepository.findByUserIdAndType(
        preferenceData.getUserId(),
        preferenceData.getType()
      );

    if (existingPreference != null) {
      throw new IllegalArgumentException(
        "Notification preference already exists for this user and type"
      );
    }

    NotificationPreference preference = notificationPreferenceMapper.toEntity(
      preferenceData
    );
    NotificationPreference savedPreference =
      notificationPreferenceRepository.save(preference);
    return notificationPreferenceMapper.toInfo(savedPreference);
  }

  /**
   * Updates an existing notification preference.
   *
   * @param id The ID of the notification preference to update
   * @param preferenceData The new data for the notification preference
   * @return The updated notification preference
   * @throws EntityNotFoundException if the notification preference is not found
   */
  @Transactional
  public NotificationPreferenceInfo update(
    Integer id,
    NotificationPreferenceData preferenceData
  ) {
    NotificationPreference preference = notificationPreferenceRepository
      .findById(id)
      .orElseThrow(EntityNotFoundException::new);

    NotificationPreference existingPreference =
      notificationPreferenceRepository.findByUserIdAndType(
        preferenceData.getUserId(),
        preferenceData.getType()
      );

    if (existingPreference != null && !existingPreference.getId().equals(id)) {
      throw new IllegalArgumentException(
        "Notification preference already exists for this user and type"
      );
    }

    notificationPreferenceMapper.updateEntityFromData(
      preferenceData,
      preference
    );
    NotificationPreference updatedPreference =
      notificationPreferenceRepository.save(preference);
    return notificationPreferenceMapper.toInfo(updatedPreference);
  }

  /**
   * Deletes a notification preference by its ID.
   *
   * @param id The ID of the notification preference to delete
   * @throws EntityNotFoundException if the notification preference is not found
   */
  @Transactional
  public void delete(Integer id) {
    NotificationPreference preference = notificationPreferenceRepository
      .findById(id)
      .orElseThrow(EntityNotFoundException::new);
    notificationPreferenceRepository.delete(preference);
  }

  /**
   * Deletes multiple notification preferences by their IDs.
   *
   * @param ids List of notification preference IDs to delete
   * @throws EntityNotFoundException if any of the notification preferences is not found
   */
  @Transactional
  public void deleteAllById(List<Integer> ids) {
    ids.forEach(id -> {
      if (!notificationPreferenceRepository.existsById(id)) {
        throw new EntityNotFoundException(
          "Notification preference with id " + id + " not found"
        );
      }
    });
    notificationPreferenceRepository.deleteAllById(ids);
  }
}
