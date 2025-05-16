package com.sigrap.employee.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.employee.Employee;
import com.sigrap.employee.EmployeeRepository;
import com.sigrap.employee.EmployeeStatus;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import com.sigrap.user.UserStatus;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
  private LocalTime defaultStartTime;
  private LocalTime defaultEndTime;

  @BeforeEach
  void setUp() {
    objectMapper.findAndRegisterModules();

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
      .hireDate(LocalDateTime.now().withNano(0))
      .status(EmployeeStatus.ACTIVE)
      .user(user)
      .build();
    testEmployee = employeeRepository.save(testEmployee);

    defaultStartTime = LocalTime.of(9, 0);
    defaultEndTime = LocalTime.of(17, 0);

    testSchedule = Schedule.builder()
      .employee(testEmployee)
      .day("MONDAY")
      .startTime(defaultStartTime)
      .endTime(defaultEndTime)
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
      .andExpect(jsonPath("$.day").value(testSchedule.getDay()))
      .andExpect(jsonPath("$.startTime").value(defaultStartTime.toString()))
      .andExpect(jsonPath("$.endTime").value(defaultEndTime.toString()));
  }

  @Test
  void findByEmployeeId_ShouldReturnSchedules() throws Exception {
    mockMvc
      .perform(
        get("/api/schedules/employee/{employeeId}", testEmployee.getId())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testSchedule.getId()))
      .andExpect(jsonPath("$[0].employeeId").value(testEmployee.getId()))
      .andExpect(jsonPath("$[0].startTime").value(defaultStartTime.toString()))
      .andExpect(jsonPath("$[0].endTime").value(defaultEndTime.toString()));
  }

  @Test
  void create_ShouldCreateSchedule() throws Exception {
    LocalTime newStartTime = LocalTime.of(10, 0);
    LocalTime newEndTime = LocalTime.of(18, 0);

    ScheduleData scheduleData = ScheduleData.builder()
      .employeeId(testEmployee.getId())
      .day("TUESDAY")
      .startTime(newStartTime)
      .endTime(newEndTime)
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
      .andExpect(jsonPath("$.day").value("TUESDAY"))
      .andExpect(jsonPath("$.startTime").value(newStartTime.toString()))
      .andExpect(jsonPath("$.endTime").value(newEndTime.toString()));
  }

  @Test
  void update_ShouldUpdateSchedule() throws Exception {
    LocalTime updatedStartTime = LocalTime.of(8, 30);
    LocalTime updatedEndTime = LocalTime.of(16, 30);

    ScheduleData updateData = ScheduleData.builder()
      .employeeId(testEmployee.getId())
      .day(testSchedule.getDay())
      .startTime(updatedStartTime)
      .endTime(updatedEndTime)
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
      .andExpect(jsonPath("$.startTime").value(updatedStartTime.toString()))
      .andExpect(jsonPath("$.endTime").value(updatedEndTime.toString()))
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
    LocalTime genStartTime = LocalTime.of(9, 0);
    LocalTime genEndTime = LocalTime.of(17, 0);
    ScheduleData scheduleData = ScheduleData.builder()
      .employeeId(testEmployee.getId())
      .day("MONDAY")
      .startTime(genStartTime)
      .endTime(genEndTime)
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
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$[0].employeeId").value(testEmployee.getId()))
      .andExpect(jsonPath("$[0].startTime").value(genStartTime.toString()));
  }
}
