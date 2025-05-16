package com.sigrap.employee.schedule;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Schedule entity.
 * Provides database operations for schedules.
 *
 * <p>This repository includes:
 * <ul>
 *   <li>Basic CRUD operations</li>
 *   <li>Custom search methods</li>
 *   <li>User-based queries</li>
 *   <li>Day-based queries</li>
 * </ul></p>
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
  /**
   * Finds all schedules for a specific user.
   *
   * @param userId ID of the user
   * @return List of schedules for the user
   */
  List<Schedule> findByUserId(Long userId);

  /**
   * Finds all schedules for a specific user on a specific day.
   *
   * @param userId ID of the user
   * @param day Day of the week
   * @return List of schedules matching the criteria
   */
  List<Schedule> findByUserIdAndDay(Long userId, String day);

  /**
   * Finds all schedules for a specific day.
   *
   * @param day Day of the week
   * @return List of schedules for the day
   */
  List<Schedule> findByDay(String day);

  /**
   * Finds all active schedules for a specific user.
   *
   * @param userId ID of the user
   * @param isActive Active status to filter by
   * @return List of active schedules for the user
   */
  List<Schedule> findByUserIdAndIsActive(Long userId, Boolean isActive);

  /**
   * Finds all active schedules for a specific day.
   *
   * @param day Day of the week
   * @param isActive Active status to filter by
   * @return List of active schedules for the day
   */
  List<Schedule> findByDayAndIsActive(String day, Boolean isActive);
}
