package com.sigrap.user;

import com.sigrap.user.UserNotificationPreference.NotificationType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for UserNotificationPreference entity operations.
 * Provides methods for notification preference-specific database operations beyond basic CRUD.
 */
@Repository
public interface UserNotificationPreferenceRepository
  extends JpaRepository<UserNotificationPreference, Long> {
  /**
   * Finds all notification preferences for a specific user.
   *
   * @param userId The ID of the user to find preferences for
   * @return List of notification preferences
   */
  List<UserNotificationPreference> findByUserId(Long userId);

  /**
   * Finds a specific notification preference by user ID and notification type.
   *
   * @param userId The ID of the user
   * @param notificationType The type of notification
   * @return Optional containing the preference if found, empty otherwise
   */
  Optional<UserNotificationPreference> findByUserIdAndNotificationType(
    Long userId,
    NotificationType notificationType
  );

  /**
   * Checks if a notification preference exists for a specific user and notification type.
   *
   * @param userId The ID of the user
   * @param notificationType The type of notification
   * @return true if a preference exists, false otherwise
   */
  boolean existsByUserIdAndNotificationType(
    Long userId,
    NotificationType notificationType
  );
}
