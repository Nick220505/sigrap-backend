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
 *   <li>Employee-based queries</li>
 *   <li>Date-based queries</li>
 * </ul></p>
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
  /**
   * Finds all attendance records for a specific employee.
   *
   * @param employeeId ID of the employee
   * @return List of attendance records for the employee
   */
  List<Attendance> findByEmployeeId(Long employeeId);

  /**
   * Finds all attendance records for a specific employee between two dates.
   *
   * @param employeeId ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of attendance records matching the criteria
   */
  List<Attendance> findByEmployeeIdAndDateBetween(
    Long employeeId,
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
   * Finds an active attendance record for an employee on a specific date.
   *
   * @param employeeId ID of the employee
   * @param date Date to search for
   * @return Optional containing the attendance record if found
   */
  Optional<Attendance> findByEmployeeIdAndDate(
    Long employeeId,
    LocalDateTime date
  );

  /**
   * Finds all attendance records for a specific employee with a specific status.
   *
   * @param employeeId ID of the employee
   * @param status Status to search for
   * @return List of attendance records matching the criteria
   */
  List<Attendance> findByEmployeeIdAndStatus(
    Long employeeId,
    AttendanceStatus status
  );

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
