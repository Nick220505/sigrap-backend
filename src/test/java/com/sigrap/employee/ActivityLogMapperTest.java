package com.sigrap.employee;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the ActivityLogMapper class.
 * Tests the mapping functionality between entities and DTOs.
 */
@ExtendWith(MockitoExtension.class)
class ActivityLogMapperTest {

  @Mock
  private EmployeeMapper employeeMapper;

  @InjectMocks
  private ActivityLogMapper activityLogMapper;

  private Employee employee;
  private ActivityLogData activityLogData;
  private ActivityLog activityLog;
  private LocalDateTime timestamp;

  @BeforeEach
  void setUp() {
    timestamp = LocalDateTime.now();

    employee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .build();

    activityLogData = ActivityLogData.builder()
      .employeeId(1L)
      .timestamp(timestamp)
      .actionType(ActivityLog.ActionType.CREATE)
      .description("Test activity")
      .moduleName("test")
      .entityId("123")
      .ipAddress("127.0.0.1")
      .build();

    activityLog = ActivityLog.builder()
      .id(1L)
      .employee(employee)
      .timestamp(timestamp)
      .actionType(ActivityLog.ActionType.CREATE)
      .description("Test activity")
      .moduleName("test")
      .entityId("123")
      .ipAddress("127.0.0.1")
      .createdAt(timestamp)
      .build();
  }

  @Test
  void toEntity_shouldMapDataToEntity() {
    ActivityLog result = activityLogMapper.toEntity(activityLogData, employee);

    assertThat(result)
      .usingRecursiveComparison()
      .ignoringFields("id", "createdAt")
      .isEqualTo(activityLog);
  }

  @Test
  void toInfo_shouldMapEntityToInfo() {
    String fullName = "John Doe";
    when(employeeMapper.getFullName(employee)).thenReturn(fullName);

    ActivityLogInfo result = activityLogMapper.toInfo(activityLog);

    assertThat(result)
      .hasFieldOrPropertyWithValue("id", activityLog.getId())
      .hasFieldOrPropertyWithValue("employeeId", employee.getId())
      .hasFieldOrPropertyWithValue("employeeName", fullName)
      .hasFieldOrPropertyWithValue("timestamp", activityLog.getTimestamp())
      .hasFieldOrPropertyWithValue("actionType", activityLog.getActionType())
      .hasFieldOrPropertyWithValue("description", activityLog.getDescription())
      .hasFieldOrPropertyWithValue("moduleName", activityLog.getModuleName())
      .hasFieldOrPropertyWithValue("entityId", activityLog.getEntityId())
      .hasFieldOrPropertyWithValue("ipAddress", activityLog.getIpAddress())
      .hasFieldOrPropertyWithValue("createdAt", activityLog.getCreatedAt());
  }
}
