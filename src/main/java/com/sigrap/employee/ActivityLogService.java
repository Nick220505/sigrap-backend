package com.sigrap.employee;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Service class for activity log management operations.
 * Handles business logic for activity logging and reporting.
 *
 * <p>This service provides:
 * <ul>
 *   <li>Activity logging operations</li>
 *   <li>Activity search functionality</li>
 *   <li>Report generation</li>
 *   <li>Transaction management</li>
 * </ul></p>
 *
 * <p>Key features:
 * <ul>
 *   <li>Transactional operations</li>
 *   <li>Employee validation</li>
 *   <li>Flexible reporting</li>
 *   <li>Activity filtering</li>
 * </ul></p>
 *
 * @see ActivityLog
 * @see ActivityLogRepository
 * @see ActivityLogMapper
 */
@Service
@RequiredArgsConstructor
public class ActivityLogService {

  private final ActivityLogRepository activityLogRepository;
  private final EmployeeRepository employeeRepository;
  private final ActivityLogMapper activityLogMapper;

  /**
   * Logs a new activity in the system.
   * Creates a new activity log entry after validating the employee.
   *
   * @param data The activity data to log
   * @return ActivityLogInfo containing the created log's information
   * @throws EntityNotFoundException if the referenced employee is not found
   */
  @Transactional
  public ActivityLogInfo logActivity(ActivityLogData data) {
    Employee employee = employeeRepository
      .findById(data.getEmployeeId())
      .orElseThrow(() ->
        new EntityNotFoundException(
          "Employee not found: " + data.getEmployeeId()
        )
      );

    ActivityLog activityLog = activityLogMapper.toEntity(data, employee);
    activityLog = activityLogRepository.save(activityLog);
    return activityLogMapper.toInfo(activityLog);
  }

  /**
   * Finds all activity logs for a specific employee.
   *
   * @param employeeId ID of the employee
   * @return List of activity logs for the employee
   */
  @Transactional(readOnly = true)
  public List<ActivityLogInfo> findByEmployeeId(Long employeeId) {
    return activityLogRepository
      .findByEmployeeId(employeeId)
      .stream()
      .map(activityLogMapper::toInfo)
      .toList();
  }

  /**
   * Finds all activity logs of a specific type.
   *
   * @param actionType Type of action to filter by
   * @return List of activity logs of the specified type
   */
  @Transactional(readOnly = true)
  public List<ActivityLogInfo> findByActionType(
    ActivityLog.ActionType actionType
  ) {
    return activityLogRepository
      .findByActionType(actionType)
      .stream()
      .map(activityLogMapper::toInfo)
      .toList();
  }

  /**
   * Finds all activity logs from a specific module.
   *
   * @param moduleName Name of the module to filter by
   * @return List of activity logs from the specified module
   */
  @Transactional(readOnly = true)
  public List<ActivityLogInfo> findByModuleName(String moduleName) {
    return activityLogRepository
      .findByModuleName(moduleName)
      .stream()
      .map(activityLogMapper::toInfo)
      .toList();
  }

  /**
   * Generates an activity report for a date range.
   * Optionally filters by employee if employeeId is provided.
   *
   * @param employeeId Optional ID of the employee to filter by
   * @param startDate Start of the date range
   * @param endDate End of the date range
   * @return List of activity logs matching the criteria
   */
  @Transactional(readOnly = true)
  public List<ActivityLogInfo> generateActivityReport(
    Long employeeId,
    LocalDateTime startDate,
    LocalDateTime endDate
  ) {
    if (employeeId != null) {
      return activityLogRepository
        .findByEmployeeIdAndTimestampBetween(employeeId, startDate, endDate)
        .stream()
        .map(activityLogMapper::toInfo)
        .toList();
    }

    return activityLogRepository
      .findByTimestampBetween(startDate, endDate)
      .stream()
      .map(activityLogMapper::toInfo)
      .toList();
  }
}
