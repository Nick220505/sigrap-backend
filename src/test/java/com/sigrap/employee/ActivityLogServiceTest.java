package com.sigrap.employee;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;

/**
 * Unit tests for the ActivityLogService class.
 * Tests the business logic for activity log management.
 */
@ExtendWith(MockitoExtension.class)
class ActivityLogServiceTest {

  @Mock
  private ActivityLogRepository activityLogRepository;

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private ActivityLogMapper activityLogMapper;

  @InjectMocks
  private ActivityLogService activityLogService;

  private Employee employee;
  private ActivityLogData activityLogData;
  private ActivityLog activityLog;
  private ActivityLogInfo activityLogInfo;
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

    activityLogInfo = ActivityLogInfo.builder()
      .id(1L)
      .employeeId(1L)
      .employeeName("John Doe")
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
  void logActivity_withValidData_shouldCreateActivityLog() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
    when(activityLogMapper.toEntity(activityLogData, employee)).thenReturn(
      activityLog
    );
    when(activityLogRepository.save(activityLog)).thenReturn(activityLog);
    when(activityLogMapper.toInfo(activityLog)).thenReturn(activityLogInfo);

    ActivityLogInfo result = activityLogService.logActivity(activityLogData);

    assertThat(result).isEqualTo(activityLogInfo);
    verify(activityLogRepository).save(activityLog);
  }

  @Test
  void logActivity_withInvalidEmployeeId_shouldThrowException() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> activityLogService.logActivity(activityLogData))
      .isInstanceOf(EntityNotFoundException.class)
      .hasMessageContaining("Employee not found: 1");
  }

  @Test
  void findByEmployeeId_shouldReturnActivityLogs() {
    List<ActivityLog> activityLogs = List.of(activityLog);
    List<ActivityLogInfo> expectedInfos = List.of(activityLogInfo);

    when(activityLogRepository.findByEmployeeId(1L)).thenReturn(activityLogs);
    when(activityLogMapper.toInfo(any(ActivityLog.class))).thenReturn(
      activityLogInfo
    );

    List<ActivityLogInfo> result = activityLogService.findByEmployeeId(1L);

    assertThat(result).isEqualTo(expectedInfos);
  }

  @Test
  void findByActionType_shouldReturnActivityLogs() {
    List<ActivityLog> activityLogs = List.of(activityLog);
    List<ActivityLogInfo> expectedInfos = List.of(activityLogInfo);

    when(
      activityLogRepository.findByActionType(ActivityLog.ActionType.CREATE)
    ).thenReturn(activityLogs);
    when(activityLogMapper.toInfo(any(ActivityLog.class))).thenReturn(
      activityLogInfo
    );

    List<ActivityLogInfo> result = activityLogService.findByActionType(
      ActivityLog.ActionType.CREATE
    );

    assertThat(result).isEqualTo(expectedInfos);
  }

  @Test
  void findByModuleName_shouldReturnActivityLogs() {
    List<ActivityLog> activityLogs = List.of(activityLog);
    List<ActivityLogInfo> expectedInfos = List.of(activityLogInfo);

    when(activityLogRepository.findByModuleName("test")).thenReturn(
      activityLogs
    );
    when(activityLogMapper.toInfo(any(ActivityLog.class))).thenReturn(
      activityLogInfo
    );

    List<ActivityLogInfo> result = activityLogService.findByModuleName("test");

    assertThat(result).isEqualTo(expectedInfos);
  }

  @Test
  void generateActivityReport_withEmployeeId_shouldReturnFilteredLogs() {
    LocalDateTime startDate = timestamp.minusDays(1);
    LocalDateTime endDate = timestamp.plusDays(1);
    List<ActivityLog> activityLogs = List.of(activityLog);
    List<ActivityLogInfo> expectedInfos = List.of(activityLogInfo);

    when(
      activityLogRepository.findByEmployeeIdAndTimestampBetween(
        1L,
        startDate,
        endDate
      )
    ).thenReturn(activityLogs);
    when(activityLogMapper.toInfo(any(ActivityLog.class))).thenReturn(
      activityLogInfo
    );

    List<ActivityLogInfo> result = activityLogService.generateActivityReport(
      1L,
      startDate,
      endDate
    );

    assertThat(result).isEqualTo(expectedInfos);
  }

  @Test
  void generateActivityReport_withoutEmployeeId_shouldReturnAllLogs() {
    LocalDateTime startDate = timestamp.minusDays(1);
    LocalDateTime endDate = timestamp.plusDays(1);
    List<ActivityLog> activityLogs = List.of(activityLog);
    List<ActivityLogInfo> expectedInfos = List.of(activityLogInfo);

    when(
      activityLogRepository.findByTimestampBetween(startDate, endDate)
    ).thenReturn(activityLogs);
    when(activityLogMapper.toInfo(any(ActivityLog.class))).thenReturn(
      activityLogInfo
    );

    List<ActivityLogInfo> result = activityLogService.generateActivityReport(
      null,
      startDate,
      endDate
    );

    assertThat(result).isEqualTo(expectedInfos);
  }
}
