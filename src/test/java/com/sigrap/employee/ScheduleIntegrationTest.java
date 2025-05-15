package com.sigrap.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(username = "test@example.com", roles = { "ADMIN" })
class ScheduleIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private ScheduleRepository scheduleRepository;

  @Autowired
  private UserRepository userRepository;

  private Employee testEmployee;
  private Schedule testSchedule;

  @BeforeEach
  void setUp() {
    User user = User.builder()
      .name("John Doe")
      .email("john.doe@example.com")
      .password("password123")
      .status(UserStatus.ACTIVE)
      .build();
    user = userRepository.save(user);

    testEmployee = Employee.builder()
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .position("Sales")
      .department("Sales")
      .hireDate(LocalDateTime.now())
      .status(Employee.EmployeeStatus.ACTIVE)
      .user(user)
      .build();
    testEmployee = employeeRepository.save(testEmployee);

    testSchedule = Schedule.builder()
      .employee(testEmployee)
      .day("MONDAY")
      .startTime(LocalDateTime.now())
      .endTime(LocalDateTime.now().plusHours(8))
      .isActive(true)
      .build();
    testSchedule = scheduleRepository.save(testSchedule);
  }

  @Test
  void findById_ShouldReturnSchedule() throws Exception {
    mockMvc
      .perform(get("/api/schedules/{id}", testSchedule.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testSchedule.getId()))
      .andExpect(jsonPath("$.employeeId").value(testEmployee.getId()))
      .andExpect(jsonPath("$.day").value(testSchedule.getDay()));
  }

  @Test
  void findByEmployeeId_ShouldReturnSchedules() throws Exception {
    mockMvc
      .perform(
        get("/api/schedules/employee/{employeeId}", testEmployee.getId())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testSchedule.getId()))
      .andExpect(jsonPath("$[0].employeeId").value(testEmployee.getId()));
  }

  @Test
  void create_ShouldCreateSchedule() throws Exception {
    ScheduleData scheduleData = ScheduleData.builder()
      .employeeId(testEmployee.getId())
      .day("TUESDAY")
      .startTime(LocalDateTime.now().plusDays(1))
      .endTime(LocalDateTime.now().plusDays(1).plusHours(8))
      .isActive(true)
      .build();

    mockMvc
      .perform(
        post("/api/schedules")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(scheduleData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.employeeId").value(testEmployee.getId()))
      .andExpect(jsonPath("$.day").value("TUESDAY"));
  }

  @Test
  void update_ShouldUpdateSchedule() throws Exception {
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(8);

    ScheduleData updateData = ScheduleData.builder()
      .employeeId(testEmployee.getId())
      .day(testSchedule.getDay())
      .startTime(startTime)
      .endTime(endTime)
      .isActive(true)
      .build();

    mockMvc
      .perform(
        put("/api/schedules/{id}", testSchedule.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updateData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.employeeId").value(testEmployee.getId()))
      .andExpect(jsonPath("$.day").value(testSchedule.getDay()))
      .andExpect(jsonPath("$.isActive").value(true));
  }

  @Test
  void delete_ShouldDeleteSchedule() throws Exception {
    mockMvc
      .perform(delete("/api/schedules/{id}", testSchedule.getId()))
      .andExpect(status().isNoContent());

    assertThat(scheduleRepository.findById(testSchedule.getId())).isEmpty();
  }

  @Test
  void generateWeeklySchedule_ShouldGenerateSchedules() throws Exception {
    ScheduleData scheduleData = ScheduleData.builder()
      .employeeId(testEmployee.getId())
      .day("MONDAY")
      .startTime(LocalDateTime.now())
      .endTime(LocalDateTime.now().plusHours(8))
      .isActive(true)
      .build();

    mockMvc
      .perform(
        post(
          "/api/schedules/generate-weekly/{employeeId}",
          testEmployee.getId()
        )
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(scheduleData))
      )
      .andExpect(status().is2xxSuccessful());
  }
}
