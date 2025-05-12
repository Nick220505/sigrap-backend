package com.sigrap.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.SecurityConfig;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ScheduleController.class)
@Import({ SecurityConfig.class })
class ScheduleControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ScheduleService scheduleService;

  private ScheduleInfo testSchedule;
  private ScheduleData testData;

  @BeforeEach
  void setUp() {
    testSchedule = ScheduleInfo.builder()
      .id(1L)
      .employeeId(1L)
      .employeeName("John Doe")
      .startTime(LocalDateTime.now())
      .endTime(LocalDateTime.now().plusHours(8))
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    testData = ScheduleData.builder()
      .employeeId(1L)
      .startTime(LocalDateTime.now())
      .endTime(LocalDateTime.now().plusHours(8))
      .build();
  }

  @Test
  @WithMockUser
  void findAll_shouldReturnSchedules() throws Exception {
    when(scheduleService.findAll()).thenReturn(List.of(testSchedule));

    mockMvc
      .perform(get("/api/schedules"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testSchedule.getId()))
      .andExpect(
        jsonPath("$[0].employeeId").value(testSchedule.getEmployeeId())
      )
      .andExpect(
        jsonPath("$[0].employeeName").value(testSchedule.getEmployeeName())
      );
  }

  @Test
  @WithMockUser
  void findById_shouldReturnSchedule() throws Exception {
    when(scheduleService.findById(anyLong())).thenReturn(testSchedule);

    mockMvc
      .perform(get("/api/schedules/{id}", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testSchedule.getId()))
      .andExpect(jsonPath("$.employeeId").value(testSchedule.getEmployeeId()))
      .andExpect(
        jsonPath("$.employeeName").value(testSchedule.getEmployeeName())
      );
  }

  @Test
  @WithMockUser
  void findByEmployeeId_shouldReturnSchedules() throws Exception {
    when(scheduleService.findByEmployeeId(anyLong())).thenReturn(
      List.of(testSchedule)
    );

    mockMvc
      .perform(get("/api/schedules/employee/{employeeId}", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testSchedule.getId()))
      .andExpect(
        jsonPath("$[0].employeeId").value(testSchedule.getEmployeeId())
      )
      .andExpect(
        jsonPath("$[0].employeeName").value(testSchedule.getEmployeeName())
      );
  }

  @Test
  @WithMockUser
  void create_withValidData_shouldCreateSchedule() throws Exception {
    when(scheduleService.create(any())).thenReturn(testSchedule);

    mockMvc
      .perform(
        post("/api/schedules")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(testSchedule.getId()))
      .andExpect(jsonPath("$.employeeId").value(testSchedule.getEmployeeId()))
      .andExpect(
        jsonPath("$.employeeName").value(testSchedule.getEmployeeName())
      );
  }

  @Test
  @WithMockUser
  void update_withValidData_shouldUpdateSchedule() throws Exception {
    when(scheduleService.update(anyLong(), any())).thenReturn(testSchedule);

    mockMvc
      .perform(
        put("/api/schedules/{id}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testSchedule.getId()))
      .andExpect(jsonPath("$.employeeId").value(testSchedule.getEmployeeId()))
      .andExpect(
        jsonPath("$.employeeName").value(testSchedule.getEmployeeName())
      );
  }

  @Test
  @WithMockUser
  void delete_shouldDeleteSchedule() throws Exception {
    mockMvc
      .perform(delete("/api/schedules/{id}", 1L))
      .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser
  void generateWeeklySchedule_shouldGenerateSchedules() throws Exception {
    when(scheduleService.generateWeeklySchedule(any(), any())).thenReturn(
      List.of(testSchedule)
    );

    mockMvc
      .perform(
        post("/api/schedules/generate-weekly")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$[0].id").value(testSchedule.getId()))
      .andExpect(
        jsonPath("$[0].employeeId").value(testSchedule.getEmployeeId())
      )
      .andExpect(
        jsonPath("$[0].employeeName").value(testSchedule.getEmployeeName())
      );
  }
}
