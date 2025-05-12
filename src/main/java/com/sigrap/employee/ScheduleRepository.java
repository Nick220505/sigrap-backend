package com.sigrap.employee;

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
 *   <li>Employee-based queries</li>
 *   <li>Day-based queries</li>
 * </ul></p>
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
  /**
   * Finds all schedules for a specific employee.
   *
   * @param employeeId ID of the employee
   * @return List of schedules for the employee
   */
  List<Schedule> findByEmployeeId(Long employeeId);

  /**
   * Finds all schedules for a specific employee on a specific day.
   *
   * @param employeeId ID of the employee
   * @param day Day of the week
   * @return List of schedules matching the criteria
   */
  List<Schedule> findByEmployeeIdAndDay(Long employeeId, String day);

  /**
   * Finds all schedules for a specific day.
   *
   * @param day Day of the week
   * @return List of schedules for the day
   */
  List<Schedule> findByDay(String day);

  /**
   * Finds all active schedules for a specific employee.
   *
   * @param employeeId ID of the employee
   * @param isActive Active status to filter by
   * @return List of active schedules for the employee
   */
  List<Schedule> findByEmployeeIdAndIsActive(Long employeeId, Boolean isActive);

  /**
   * Finds all active schedules for a specific day.
   *
   * @param day Day of the week
   * @param isActive Active status to filter by
   * @return List of active schedules for the day
   */
  List<Schedule> findByDayAndIsActive(String day, Boolean isActive);
}
