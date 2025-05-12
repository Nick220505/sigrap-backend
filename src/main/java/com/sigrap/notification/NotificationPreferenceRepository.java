package com.sigrap.notification;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for NotificationPreference entity operations.
 * Provides methods for notification preference-specific database operations beyond basic CRUD.
 */
@Repository
public interface NotificationPreferenceRepository
  extends JpaRepository<NotificationPreference, Integer> {
  /**
   * Finds all notification preferences for a specific user.
   *
   * @param userId The ID of the user to find preferences for
   * @return List of matching notification preferences
   */
  List<NotificationPreference> findByUserIdOrderByTypeAsc(Long userId);

  /**
   * Finds a specific notification preference for a user.
   *
   * @param userId The ID of the user
   * @param type The type of notification
   * @return The matching notification preference, if any
   */
  NotificationPreference findByUserIdAndType(
    Long userId,
    NotificationType type
  );
}
