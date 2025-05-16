package com.sigrap.employee.attendance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Attendance entity.
 * Provides database operations for attendance records.
 *
 * <p>This repository includes:
 * <ul>
 *   <li>Basic CRUD operations</li>
 *   <li>Custom search methods</li>
 *   <li>User-based queries</li>
 *   <li>Date-based queries</li>
 * </ul></p>
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
  /**
   * Finds all attendance records for a specific user.
   *
   * @param userId ID of the user
   * @return List of attendance records for the user
   */
  List<Attendance> findByUserId(Long userId);

  /**
   * Finds all attendance records for a specific user between two dates.
   *
   * @param userId ID of the user
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of attendance records matching the criteria
   */
  List<Attendance> findByUserIdAndDateBetween(
    Long userId,
    LocalDateTime startDate,
    LocalDateTime endDate
  );

  /**
   * Finds all attendance records for a specific date.
   *
   * @param date Date to search for
   * @return List of attendance records for the date
   */
  List<Attendance> findByDate(LocalDateTime date);

  /**
   * Finds all attendance records with a specific status.
   *
   * @param status Status to search for
   * @return List of attendance records with the status
   */
  List<Attendance> findByStatus(AttendanceStatus status);

  /**
   * Finds an active attendance record for a user on a specific date.
   *
   * @param userId ID of the user
   * @param date Date to search for
   * @return Optional containing the attendance record if found
   */
  Optional<Attendance> findByUserIdAndDate(Long userId, LocalDateTime date);

  /**
   * Finds all attendance records for a specific user with a specific status.
   *
   * @param userId ID of the user
   * @param status Status to search for
   * @return List of attendance records matching the criteria
   */
  List<Attendance> findByUserIdAndStatus(Long userId, AttendanceStatus status);

  /**
   * Finds all attendance records between two dates.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of attendance records within the date range
   */
  List<Attendance> findByDateBetween(
    LocalDateTime startDate,
    LocalDateTime endDate
  );
}
