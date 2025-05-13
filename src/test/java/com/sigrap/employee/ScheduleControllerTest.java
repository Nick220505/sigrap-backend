package com.sigrap.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

class ScheduleControllerTest {

  private MockMvc mockMvc;
  private ScheduleService scheduleService;
  private ObjectMapper objectMapper;

  @ControllerAdvice
  public static class TestExceptionHandler
    extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(
      EntityNotFoundException ex
    ) {
      return ResponseEntity.notFound().build();
    }
  }

  private ScheduleInfo testSchedule;
  private ScheduleData testData;

  @BeforeEach
  void setUp() {
    scheduleService = mock(ScheduleService.class);
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    ScheduleController controller = new ScheduleController(scheduleService);

    mockMvc = standaloneSetup(controller)
      .setControllerAdvice(new TestExceptionHandler())
      .build();

    testSchedule = ScheduleInfo.builder()
      .id(1L)
      .employeeId(1L)
      .employeeName("John Doe")
      .startTime(LocalDateTime.now())
      .endTime(LocalDateTime.now().plusHours(8))
      .day("MONDAY")
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    testData = ScheduleData.builder()
      .employeeId(1L)
      .startTime(LocalDateTime.now())
      .endTime(LocalDateTime.now().plusHours(8))
      .day("MONDAY")
      .build();
  }

  @Test
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
  void delete_shouldDeleteSchedule() throws Exception {
    mockMvc
      .perform(delete("/api/schedules/{id}", 1L))
      .andExpect(status().isNoContent());
  }

  @Test
  void generateWeeklySchedule_shouldGenerateSchedules() throws Exception {
    when(scheduleService.generateWeeklySchedule(any(), any())).thenReturn(
      List.of(testSchedule)
    );

    mockMvc
      .perform(
        post("/api/schedules/generate-weekly/{employeeId}", 1L)
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
