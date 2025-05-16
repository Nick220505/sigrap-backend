package com.sigrap.employee.attendance;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.employee.Employee;
import com.sigrap.employee.EmployeeRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AttendanceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private AttendanceRepository attendanceRepository;

  @Autowired
  private UserRepository userRepository;

  private Employee testEmployee;
  private Attendance testAttendance;
  private ClockInData testClockInData;
  private ClockOutData testClockOutData;

  @BeforeEach
  void setUp() {
    String uniqueEmail = "john" + System.currentTimeMillis() + "@example.com";
    String uniqueDocumentId = "DOC" + System.currentTimeMillis();

    User user = User.builder()
      .name("John Doe")
      .email(uniqueEmail)
      .password("password")
      .status(UserStatus.ACTIVE)
      .build();
    userRepository.save(user);

    testEmployee = Employee.builder()
      .firstName("John")
      .lastName("Doe")
      .documentId(uniqueDocumentId)
      .position("Sales")
      .department("Sales")
      .hireDate(LocalDateTime.now())
      .user(user)
      .build();
    employeeRepository.save(testEmployee);

    testAttendance = Attendance.builder()
      .employee(testEmployee)
      .date(LocalDateTime.now())
      .clockInTime(LocalDateTime.now())
      .status(AttendanceStatus.PRESENT)
      .notes("Regular day")
      .build();
    attendanceRepository.save(testAttendance);

    testClockInData = ClockInData.builder()
      .employeeId(testEmployee.getId())
      .timestamp(LocalDateTime.now())
      .notes("On time")
      .build();

    testClockOutData = ClockOutData.builder()
      .attendanceId(testAttendance.getId())
      .timestamp(LocalDateTime.now().plusHours(8))
      .notes("Regular end of shift")
      .build();
  }

  @Test
  void findAll_ShouldReturnAllAttendanceRecords() throws Exception {
    mockMvc
      .perform(get("/api/attendance"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(greaterThan(0)));
  }

  @Test
  void clockIn_ShouldCreateNewAttendanceRecord() throws Exception {
    mockMvc
      .perform(
        post("/api/attendance/clock-in")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testClockInData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.employeeId").value(testEmployee.getId()));
  }

  @Test
  void clockOut_ShouldUpdateAttendanceRecord() throws Exception {
    mockMvc
      .perform(
        put("/api/attendance/clock-out")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testClockOutData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testAttendance.getId()))
      .andExpect(jsonPath("$.employeeId").value(testEmployee.getId()));
  }

  @Test
  void findByEmployeeId_ShouldReturnEmployeeAttendanceRecords()
    throws Exception {
    mockMvc
      .perform(get("/api/attendance/employee/" + testEmployee.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testAttendance.getId()))
      .andExpect(jsonPath("$[0].employeeId").value(testEmployee.getId()));
  }

  @Test
  void generateAttendanceReport_ShouldReturnReport() throws Exception {
    LocalDateTime startDate = LocalDateTime.now().minusDays(7);
    LocalDateTime endDate = LocalDateTime.now();

    mockMvc
      .perform(
        get("/api/attendance/report")
          .param("employeeId", testEmployee.getId().toString())
          .param("startDate", startDate.toString())
          .param("endDate", endDate.toString())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testAttendance.getId()));
  }

  @Test
  void updateStatus_ShouldUpdateAttendanceStatus() throws Exception {
    Attendance freshAttendance = Attendance.builder()
      .employee(testEmployee)
      .date(LocalDateTime.now())
      .clockInTime(LocalDateTime.now())
      .clockOutTime(LocalDateTime.now().plusHours(8))
      .totalHours(8.0)
      .status(AttendanceStatus.PRESENT)
      .notes("")
      .build();

    attendanceRepository.save(freshAttendance);

    mockMvc
      .perform(
        put("/api/attendance/" + freshAttendance.getId() + "/status")
          .param("status", AttendanceStatus.LATE.toString())
          .param("notes", "Late arrival")
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(AttendanceStatus.LATE.toString()));

    Attendance updatedAttendance = attendanceRepository
      .findById(freshAttendance.getId())
      .orElse(null);
    assertNotNull(updatedAttendance);
    assertEquals(AttendanceStatus.LATE, updatedAttendance.getStatus());
    assertEquals("Late arrival", updatedAttendance.getNotes());
  }
}
