package com.sigrap.employee.attendance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

class AttendanceControllerTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private AttendanceService attendanceService;

  private AttendanceInfo testAttendanceInfo;
  private ClockInData testClockInData;
  private ClockOutData testClockOutData;

  @BeforeEach
  void setUp() {
    attendanceService = mock(AttendanceService.class);

    AttendanceController attendanceController = new AttendanceController(
      attendanceService
    );

    mockMvc = standaloneSetup(attendanceController).build();

    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    testAttendanceInfo = AttendanceInfo.builder()
      .id(1L)
      .userId(1L)
      .userName("John Doe")
      .date(LocalDateTime.now())
      .clockInTime(LocalDateTime.now())
      .clockOutTime(LocalDateTime.now().plusHours(8))
      .totalHours(8.0)
      .status(AttendanceStatus.PRESENT)
      .build();

    testClockInData = ClockInData.builder()
      .userId(1L)
      .timestamp(LocalDateTime.now())
      .build();

    testClockOutData = ClockOutData.builder()
      .attendanceId(1L)
      .timestamp(LocalDateTime.now().plusHours(8))
      .build();
  }

  @Test
  void findAll_ShouldReturnAllAttendanceRecords() throws Exception {
    when(attendanceService.findAll()).thenReturn(List.of(testAttendanceInfo));

    mockMvc
      .perform(get("/api/attendance"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testAttendanceInfo.getId()))
      .andExpect(jsonPath("$[0].userId").value(testAttendanceInfo.getUserId()));
  }

  @Test
  void clockIn_ShouldCreateNewAttendanceRecord() throws Exception {
    when(
      attendanceService.clockIn(
        eq(testClockInData.getUserId()),
        any(LocalDateTime.class)
      )
    ).thenReturn(testAttendanceInfo);

    mockMvc
      .perform(
        post("/api/attendance/clock-in")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testClockInData))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(testAttendanceInfo.getId()))
      .andExpect(jsonPath("$.userId").value(testAttendanceInfo.getUserId()));
  }

  @Test
  void clockOut_ShouldUpdateAttendanceRecord() throws Exception {
    when(
      attendanceService.clockOut(
        eq(testClockOutData.getAttendanceId()),
        any(LocalDateTime.class)
      )
    ).thenReturn(testAttendanceInfo);

    mockMvc
      .perform(
        put("/api/attendance/clock-out")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(testClockOutData))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testAttendanceInfo.getId()))
      .andExpect(jsonPath("$.userId").value(testAttendanceInfo.getUserId()));
  }

  @Test
  void findByUserId_ShouldReturnUserAttendanceRecords() throws Exception {
    when(attendanceService.findByUserId(1L)).thenReturn(
      List.of(testAttendanceInfo)
    );

    mockMvc
      .perform(get("/api/attendance/user/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testAttendanceInfo.getId()))
      .andExpect(jsonPath("$[0].userId").value(testAttendanceInfo.getUserId()));
  }

  @Test
  void generateAttendanceReport_ShouldReturnReport() throws Exception {
    LocalDateTime startDate = LocalDateTime.now().minusDays(7);
    LocalDateTime endDate = LocalDateTime.now();

    when(
      attendanceService.generateAttendanceReport(
        eq(1L),
        any(LocalDateTime.class),
        any(LocalDateTime.class)
      )
    ).thenReturn(List.of(testAttendanceInfo));

    mockMvc
      .perform(
        get("/api/attendance/report")
          .param("userId", "1")
          .param("startDate", startDate.toString())
          .param("endDate", endDate.toString())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testAttendanceInfo.getId()));
  }
}
