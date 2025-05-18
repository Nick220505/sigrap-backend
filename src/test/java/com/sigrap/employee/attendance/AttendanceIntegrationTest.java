package com.sigrap.employee.attendance;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigrap.config.BaseIntegrationTest;
import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class AttendanceIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private AttendanceRepository attendanceRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private User testUser;
  private Attendance testAttendance;

  @BeforeEach
  void setUp() {
    // Clean up existing data
    attendanceRepository.deleteAll();

    // Create test user
    testUser = User.builder()
      .name("John Doe")
      .email("john.doe@example.com")
      .password("password123")
      .documentId("12345678900")
      .build();
    testUser = userRepository.save(testUser);

    // Create test attendance record
    testAttendance = Attendance.builder()
      .user(testUser)
      .date(LocalDateTime.now())
      .clockInTime(LocalDateTime.now())
      .status(AttendanceStatus.PRESENT)
      .build();
    testAttendance = attendanceRepository.save(testAttendance);
  }

  @AfterEach
  void tearDown() {
    attendanceRepository.deleteAll();
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void crudOperations() throws Exception {
    // Get all attendance records
    mockMvc
      .perform(get("/api/attendance"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testAttendance.getId()))
      .andExpect(
        jsonPath("$[0].status").value(testAttendance.getStatus().toString())
      )
      .andExpect(jsonPath("$[0].userId").value(testUser.getId()));

    // Get attendance by ID
    mockMvc
      .perform(get("/api/attendance/{id}", testAttendance.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testAttendance.getId()))
      .andExpect(
        jsonPath("$.status").value(testAttendance.getStatus().toString())
      )
      .andExpect(jsonPath("$.userId").value(testUser.getId()));

    // Create a new attendance record
    AttendanceData newAttendanceData = AttendanceData.builder()
      .userId(testUser.getId())
      .date(LocalDateTime.now())
      .clockInTime(LocalDateTime.now())
      .status(AttendanceStatus.PRESENT)
      .build();

    MvcResult result = mockMvc
      .perform(
        post("/api/attendance")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(newAttendanceData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.userId").value(testUser.getId()))
      .andExpect(
        jsonPath("$.status").value(AttendanceStatus.PRESENT.toString())
      )
      .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    AttendanceInfo createdAttendance = objectMapper.readValue(
      responseContent,
      AttendanceInfo.class
    );

    // Update the attendance record
    AttendanceData updateData = AttendanceData.builder()
      .userId(testUser.getId())
      .date(LocalDateTime.now())
      .clockInTime(LocalDateTime.now())
      .clockOutTime(LocalDateTime.now().plusHours(8))
      .status(AttendanceStatus.PRESENT)
      .build();

    mockMvc
      .perform(
        put("/api/attendance/{id}", createdAttendance.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(updateData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(createdAttendance.getId()))
      .andExpect(jsonPath("$.clockOutTime").isNotEmpty());

    // Delete the attendance record
    mockMvc
      .perform(delete("/api/attendance/{id}", createdAttendance.getId()))
      .andExpect(status().isNoContent());

    // Verify the attendance was deleted
    mockMvc
      .perform(get("/api/attendance/{id}", createdAttendance.getId()))
      .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findByUser() throws Exception {
    // Create another attendance record for the same user
    Attendance secondAttendance = Attendance.builder()
      .user(testUser)
      .date(LocalDateTime.now().minusDays(1))
      .clockInTime(LocalDateTime.now().minusDays(1))
      .clockOutTime(LocalDateTime.now().minusDays(1).plusHours(8))
      .status(AttendanceStatus.PRESENT)
      .build();

    secondAttendance = attendanceRepository.save(secondAttendance);

    // Find attendance records by user ID
    mockMvc
      .perform(get("/api/attendance/user/{userId}", testUser.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].userId").value(testUser.getId()))
      .andExpect(jsonPath("$[1].userId").value(testUser.getId()));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findByDateRange() throws Exception {
    // Create attendance records for different dates
    LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);

    Attendance yesterdayAttendance = Attendance.builder()
      .user(testUser)
      .date(yesterday)
      .clockInTime(yesterday)
      .clockOutTime(yesterday.plusHours(8))
      .status(AttendanceStatus.PRESENT)
      .build();

    Attendance lastWeekAttendance = Attendance.builder()
      .user(testUser)
      .date(lastWeek)
      .clockInTime(lastWeek)
      .clockOutTime(lastWeek.plusHours(8))
      .status(AttendanceStatus.PRESENT)
      .build();

    attendanceRepository.saveAll(
      Arrays.asList(yesterdayAttendance, lastWeekAttendance)
    );

    // Find attendance records by date range
    String startDate = yesterday.toString();
    String endDate = LocalDateTime.now().toString();

    mockMvc
      .perform(
        get("/api/attendance/date-range")
          .param("startDate", startDate)
          .param("endDate", endDate)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void findByStatus() throws Exception {
    // Create attendance records with different statuses
    Attendance lateAttendance = Attendance.builder()
      .user(testUser)
      .date(LocalDateTime.now().minusDays(1))
      .clockInTime(LocalDateTime.now().minusDays(1).plusHours(1)) // 1 hour late
      .status(AttendanceStatus.LATE)
      .build();

    Attendance absentAttendance = Attendance.builder()
      .user(testUser)
      .date(LocalDateTime.now().minusDays(2))
      .status(AttendanceStatus.ABSENT)
      .build();

    attendanceRepository.saveAll(
      Arrays.asList(lateAttendance, absentAttendance)
    );

    // Find attendance records by status LATE
    mockMvc
      .perform(
        get("/api/attendance/status/{status}", AttendanceStatus.LATE.toString())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(
        jsonPath("$[0].status").value(AttendanceStatus.LATE.toString())
      );

    // Find attendance records by status ABSENT
    mockMvc
      .perform(
        get(
          "/api/attendance/status/{status}",
          AttendanceStatus.ABSENT.toString()
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(
        jsonPath("$[0].status").value(AttendanceStatus.ABSENT.toString())
      );

    // Find attendance records by status PRESENT
    mockMvc
      .perform(
        get(
          "/api/attendance/status/{status}",
          AttendanceStatus.PRESENT.toString()
        )
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(
        jsonPath("$[0].status").value(AttendanceStatus.PRESENT.toString())
      );
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void validationConstraints() throws Exception {
    // Attempt to create an attendance record with missing required fields
    AttendanceData invalidAttendanceData = AttendanceData.builder().build();

    mockMvc
      .perform(
        post("/api/attendance")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidAttendanceData))
      )
      .andExpect(status().isBadRequest());

    // Attempt to create an attendance record with non-existent user
    AttendanceData invalidUserData = AttendanceData.builder()
      .userId(999L)
      .date(LocalDateTime.now())
      .clockInTime(LocalDateTime.now())
      .status(AttendanceStatus.PRESENT)
      .build();

    mockMvc
      .perform(
        post("/api/attendance")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(invalidUserData))
      )
      .andExpect(status().isNotFound());
  }
}
