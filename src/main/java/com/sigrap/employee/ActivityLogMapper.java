package com.sigrap.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between ActivityLog entities and DTOs.
 * Handles the transformation of activity log data between different representations.
 *
 * <p>This class provides:
 * <ul>
 *   <li>Entity to DTO conversion</li>
 *   <li>DTO to entity conversion</li>
 *   <li>Employee name resolution</li>
 *   <li>Clean separation of layers</li>
 * </ul></p>
 *
 * <p>Key features:
 * <ul>
 *   <li>Immutable transformations</li>
 *   <li>Null-safe operations</li>
 *   <li>Complete data mapping</li>
 *   <li>Dependency injection</li>
 * </ul></p>
 *
 * @see ActivityLog
 * @see ActivityLogData
 * @see ActivityLogInfo
 */
@Component
@RequiredArgsConstructor
public class ActivityLogMapper {

  private final EmployeeMapper employeeMapper;

  /**
   * Converts activity log data to an entity.
   * Creates a new ActivityLog entity from the provided data and employee.
   *
   * @param data The activity log data to convert
   * @param employee The employee who performed the action
   * @return New ActivityLog entity with the provided data
   */
  public ActivityLog toEntity(ActivityLogData data, Employee employee) {
    return ActivityLog.builder()
      .employee(employee)
      .timestamp(data.getTimestamp())
      .actionType(data.getActionType())
      .description(data.getDescription())
      .moduleName(data.getModuleName())
      .entityId(data.getEntityId())
      .ipAddress(data.getIpAddress())
      .build();
  }

  /**
   * Converts an activity log entity to an info DTO.
   * Creates a new ActivityLogInfo with complete activity details.
   *
   * @param activityLog The activity log entity to convert
   * @return ActivityLogInfo containing the activity's information
   */
  public ActivityLogInfo toInfo(ActivityLog activityLog) {
    return ActivityLogInfo.builder()
      .id(activityLog.getId())
      .employeeId(activityLog.getEmployee().getId())
      .employeeName(employeeMapper.getFullName(activityLog.getEmployee()))
      .timestamp(activityLog.getTimestamp())
      .actionType(activityLog.getActionType())
      .description(activityLog.getDescription())
      .moduleName(activityLog.getModuleName())
      .entityId(activityLog.getEntityId())
      .ipAddress(activityLog.getIpAddress())
      .createdAt(activityLog.getCreatedAt())
      .build();
  }
}
