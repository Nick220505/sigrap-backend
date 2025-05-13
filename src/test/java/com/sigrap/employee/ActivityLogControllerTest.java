package com.sigrap.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for the ActivityLogController class.
 * Tests the REST endpoints for activity log management.
 */
class ActivityLogControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private ActivityLogService activityLogService;

  private ActivityLogData activityLogData;
  private ActivityLogInfo activityLogInfo;
  private LocalDateTime timestamp;

  @BeforeEach
  void setUp() throws Exception {
    // Setup mock service
    activityLogService = mock(ActivityLogService.class);

    // Setup controller with mocked service
    ActivityLogController activityLogController = new ActivityLogController(
      activityLogService
    );

    // Setup MockMvc
    mockMvc = standaloneSetup(activityLogController).build();

    // Configure ObjectMapper for handling LocalDateTime
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    timestamp = LocalDateTime.now();

    activityLogData = ActivityLogData.builder()
      .employeeId(1L)
      .timestamp(timestamp)
      .actionType(ActivityLog.ActionType.CREATE)
      .description("Test activity")
      .moduleName("test")
      .entityId("123")
      .ipAddress("127.0.0.1")
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
  void logActivity_withValidData_shouldCreateActivityLog() throws Exception {
    when(activityLogService.logActivity(any(ActivityLogData.class))).thenReturn(
      activityLogInfo
    );

    mockMvc
      .perform(
        post("/api/activity-logs")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(activityLogData))
      )
      .andExpect(status().isCreated())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(activityLogInfo.getId()))
      .andExpect(
        jsonPath("$.employeeId").value(activityLogInfo.getEmployeeId())
      )
      .andExpect(
        jsonPath("$.employeeName").value(activityLogInfo.getEmployeeName())
      )
      .andExpect(
        jsonPath("$.actionType").value(activityLogInfo.getActionType().name())
      )
      .andExpect(
        jsonPath("$.description").value(activityLogInfo.getDescription())
      )
      .andExpect(
        jsonPath("$.moduleName").value(activityLogInfo.getModuleName())
      )
      .andExpect(jsonPath("$.entityId").value(activityLogInfo.getEntityId()))
      .andExpect(jsonPath("$.ipAddress").value(activityLogInfo.getIpAddress()));
  }

  @Test
  void findByEmployeeId_shouldReturnActivityLogs() throws Exception {
    List<ActivityLogInfo> activityLogs = List.of(activityLogInfo);
    when(activityLogService.findByEmployeeId(1L)).thenReturn(activityLogs);

    mockMvc
      .perform(get("/api/activity-logs/employee/1"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].id").value(activityLogInfo.getId()))
      .andExpect(
        jsonPath("$[0].employeeId").value(activityLogInfo.getEmployeeId())
      )
      .andExpect(
        jsonPath("$[0].employeeName").value(activityLogInfo.getEmployeeName())
      );
  }

  @Test
  void findByActionType_shouldReturnActivityLogs() throws Exception {
    List<ActivityLogInfo> activityLogs = List.of(activityLogInfo);
    when(
      activityLogService.findByActionType(ActivityLog.ActionType.CREATE)
    ).thenReturn(activityLogs);

    mockMvc
      .perform(get("/api/activity-logs/action-type/CREATE"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].id").value(activityLogInfo.getId()))
      .andExpect(
        jsonPath("$[0].actionType").value(
          activityLogInfo.getActionType().name()
        )
      );
  }

  @Test
  void findByModuleName_shouldReturnActivityLogs() throws Exception {
    List<ActivityLogInfo> activityLogs = List.of(activityLogInfo);
    when(activityLogService.findByModuleName("test")).thenReturn(activityLogs);

    mockMvc
      .perform(get("/api/activity-logs/module/test"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].id").value(activityLogInfo.getId()))
      .andExpect(
        jsonPath("$[0].moduleName").value(activityLogInfo.getModuleName())
      );
  }

  @Test
  void generateActivityReport_shouldReturnActivityLogs() throws Exception {
    List<ActivityLogInfo> activityLogs = List.of(activityLogInfo);
    LocalDateTime startDate = timestamp.minusDays(1);
    LocalDateTime endDate = timestamp.plusDays(1);

    when(
      activityLogService.generateActivityReport(1L, startDate, endDate)
    ).thenReturn(activityLogs);

    mockMvc
      .perform(
        get("/api/activity-logs/report")
          .param("employeeId", "1")
          .param("startDate", startDate.toString())
          .param("endDate", endDate.toString())
      )
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$[0].id").value(activityLogInfo.getId()))
      .andExpect(
        jsonPath("$[0].employeeId").value(activityLogInfo.getEmployeeId())
      )
      .andExpect(
        jsonPath("$[0].timestamp").value(
          activityLogInfo.getTimestamp().toString()
        )
      );
  }
}
