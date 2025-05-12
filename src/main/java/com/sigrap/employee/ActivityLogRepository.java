package com.sigrap.employee;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for ActivityLog entity operations.
 * Provides data access methods for activity log management.
 *
 * <p>This repository supports:
 * <ul>
 *   <li>Basic CRUD operations</li>
 *   <li>Employee-based filtering</li>
 *   <li>Date range queries</li>
 *   <li>Action type filtering</li>
 * </ul></p>
 *
 * <p>Key features:
 * <ul>
 *   <li>Spring Data JPA integration</li>
 *   <li>Method name query generation</li>
 *   <li>Transaction management</li>
 *   <li>Custom query methods</li>
 * </ul></p>
 *
 * @see ActivityLog
 * @see ActivityLogService
 */
@Repository
public interface ActivityLogRepository
  extends JpaRepository<ActivityLog, Long> {
  /**
   * Finds all activity logs for a specific employee.
   *
   * @param employeeId ID of the employee
   * @return List of activity logs for the employee
   */
  List<ActivityLog> findByEmployeeId(Long employeeId);

  /**
   * Finds all activity logs for an employee within a date range.
   *
   * @param employeeId ID of the employee
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of activity logs matching the criteria
   */
  List<ActivityLog> findByEmployeeIdAndTimestampBetween(
    Long employeeId,
    LocalDateTime startDate,
    LocalDateTime endDate
  );

  /**
   * Finds all activity logs of a specific type.
   *
   * @param actionType Type of action to filter by
   * @return List of activity logs of the specified type
   */
  List<ActivityLog> findByActionType(ActivityLog.ActionType actionType);

  /**
   * Finds all activity logs from a specific module.
   *
   * @param moduleName Name of the module to filter by
   * @return List of activity logs from the specified module
   */
  List<ActivityLog> findByModuleName(String moduleName);

  /**
   * Finds all activity logs within a date range.
   *
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of activity logs within the date range
   */
  List<ActivityLog> findByTimestampBetween(
    LocalDateTime startDate,
    LocalDateTime endDate
  );
}
