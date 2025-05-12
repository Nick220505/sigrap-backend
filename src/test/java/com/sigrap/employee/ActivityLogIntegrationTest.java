package com.sigrap.employee;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.common.TestUtils;

/**
 * Integration tests for the Activity Log feature.
 * Tests the complete feature stack from controller to repository.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ActivityLogIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ActivityLogRepository activityLogRepository;

  @Autowired
  private EmployeeRepository employeeRepository;

  private Employee employee;
  private ActivityLogData activityLogData;
  private String token;
  private LocalDateTime timestamp;

  @BeforeEach
  void setUp() throws Exception {
    timestamp = LocalDateTime.now();

    employee = Employee.builder()
      .firstName("John")
      .lastName("Doe")
      .email("john@example.com")
      .build();
    employee = employeeRepository.save(employee);

    activityLogData = ActivityLogData.builder()
      .employeeId(employee.getId())
      .timestamp(timestamp)
      .actionType(ActivityLog.ActionType.CREATE)
      .description("Test activity")
      .moduleName("test")
      .entityId("123")
      .ipAddress("127.0.0.1")
      .build();

    token = TestUtils.registerTestUserAndGetToken(
      mockMvc,
      objectMapper,
      "Test User",
      "test@example.com",
      "password123"
    );
  }

  @Test
  void shouldCreateAndRetrieveActivityLog() throws Exception {
    String response = mockMvc
      .perform(
        post("/api/activity-logs")
          .header("Authorization", "Bearer " + token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(activityLogData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.employeeId").value(employee.getId()))
      .andExpect(jsonPath("$.employeeName").value("John Doe"))
      .andExpect(jsonPath("$.actionType").value("CREATE"))
      .andExpect(jsonPath("$.description").value("Test activity"))
      .andExpect(jsonPath("$.moduleName").value("test"))
      .andExpect(jsonPath("$.entityId").value("123"))
      .andExpect(jsonPath("$.ipAddress").value("127.0.0.1"))
      .andReturn()
      .getResponse()
      .getContentAsString();

    ActivityLogInfo createdLog = objectMapper.readValue(
      response,
      ActivityLogInfo.class
    );

    mockMvc
      .perform(
        get("/api/activity-logs/employee/" + employee.getId()).header(
          "Authorization",
          "Bearer " + token
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(createdLog.getId()))
      .andExpect(jsonPath("$[0].employeeId").value(employee.getId()))
      .andExpect(jsonPath("$[0].employeeName").value("John Doe"));

    assertThat(activityLogRepository.findById(createdLog.getId()))
      .isPresent()
      .hasValueSatisfying(log ->
        assertThat(log)
          .hasFieldOrPropertyWithValue("employee", employee)
          .hasFieldOrPropertyWithValue(
            "actionType",
            ActivityLog.ActionType.CREATE
          )
          .hasFieldOrPropertyWithValue("description", "Test activity")
          .hasFieldOrPropertyWithValue("moduleName", "test")
          .hasFieldOrPropertyWithValue("entityId", "123")
          .hasFieldOrPropertyWithValue("ipAddress", "127.0.0.1")
      );
  }

  @Test
  void shouldGenerateActivityReport() throws Exception {
    ActivityLog log1 = ActivityLog.builder()
      .employee(employee)
      .timestamp(timestamp)
      .actionType(ActivityLog.ActionType.CREATE)
      .description("Test activity 1")
      .moduleName("test")
      .entityId("123")
      .ipAddress("127.0.0.1")
      .build();
    activityLogRepository.save(log1);

    ActivityLog log2 = ActivityLog.builder()
      .employee(employee)
      .timestamp(timestamp.plusHours(1))
      .actionType(ActivityLog.ActionType.UPDATE)
      .description("Test activity 2")
      .moduleName("test")
      .entityId("456")
      .ipAddress("127.0.0.1")
      .build();
    activityLogRepository.save(log2);

    LocalDateTime startDate = timestamp.minusHours(1);
    LocalDateTime endDate = timestamp.plusHours(2);

    mockMvc
      .perform(
        get("/api/activity-logs/report")
          .header("Authorization", "Bearer " + token)
          .param("employeeId", employee.getId().toString())
          .param("startDate", startDate.toString())
          .param("endDate", endDate.toString())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].employeeId").value(employee.getId()))
      .andExpect(jsonPath("$[0].employeeName").value("John Doe"))
      .andExpect(jsonPath("$[0].description").value("Test activity 1"))
      .andExpect(jsonPath("$[1].description").value("Test activity 2"));
  }

  @Test
  void shouldFilterByActionType() throws Exception {
    ActivityLog log1 = ActivityLog.builder()
      .employee(employee)
      .timestamp(timestamp)
      .actionType(ActivityLog.ActionType.CREATE)
      .description("Test activity 1")
      .moduleName("test")
      .entityId("123")
      .ipAddress("127.0.0.1")
      .build();
    activityLogRepository.save(log1);

    ActivityLog log2 = ActivityLog.builder()
      .employee(employee)
      .timestamp(timestamp.plusHours(1))
      .actionType(ActivityLog.ActionType.UPDATE)
      .description("Test activity 2")
      .moduleName("test")
      .entityId("456")
      .ipAddress("127.0.0.1")
      .build();
    activityLogRepository.save(log2);

    mockMvc
      .perform(
        get("/api/activity-logs/action-type/CREATE").header(
          "Authorization",
          "Bearer " + token
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].actionType").value("CREATE"))
      .andExpect(jsonPath("$[0].description").value("Test activity 1"));
  }

  @Test
  void shouldFilterByModuleName() throws Exception {
    ActivityLog log1 = ActivityLog.builder()
      .employee(employee)
      .timestamp(timestamp)
      .actionType(ActivityLog.ActionType.CREATE)
      .description("Test activity 1")
      .moduleName("test")
      .entityId("123")
      .ipAddress("127.0.0.1")
      .build();
    activityLogRepository.save(log1);

    ActivityLog log2 = ActivityLog.builder()
      .employee(employee)
      .timestamp(timestamp.plusHours(1))
      .actionType(ActivityLog.ActionType.UPDATE)
      .description("Test activity 2")
      .moduleName("other")
      .entityId("456")
      .ipAddress("127.0.0.1")
      .build();
    activityLogRepository.save(log2);

    mockMvc
      .perform(
        get("/api/activity-logs/module/test").header(
          "Authorization",
          "Bearer " + token
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].moduleName").value("test"))
      .andExpect(jsonPath("$[0].description").value("Test activity 1"));
  }
}
