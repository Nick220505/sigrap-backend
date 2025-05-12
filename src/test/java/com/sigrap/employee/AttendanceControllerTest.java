package com.sigrap.employee;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AttendanceController.class)
class AttendanceControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Mock
  private AttendanceService attendanceService;

  private AttendanceInfo testAttendanceInfo;
  private ClockInData testClockInData;
  private ClockOutData testClockOutData;

  @BeforeEach
  void setUp() {
    testAttendanceInfo = AttendanceInfo.builder()
      .id(1L)
      .employeeId(1L)
      .employeeName("John Doe")
      .date(LocalDateTime.now())
      .clockInTime(LocalDateTime.now())
      .clockOutTime(LocalDateTime.now().plusHours(8))
      .totalHours(8.0)
      .status(Attendance.AttendanceStatus.PRESENT)
      .notes("Regular day")
      .build();

    testClockInData = ClockInData.builder()
      .employeeId(1L)
      .timestamp(LocalDateTime.now())
      .notes("On time")
      .build();

    testClockOutData = ClockOutData.builder()
      .attendanceId(1L)
      .timestamp(LocalDateTime.now().plusHours(8))
      .notes("Regular end of shift")
      .build();
  }

  @Test
  void findAll_ShouldReturnAllAttendanceRecords() throws Exception {
    when(attendanceService.findAll()).thenReturn(List.of(testAttendanceInfo));

    mockMvc
      .perform(get("/api/attendance"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testAttendanceInfo.getId()))
      .andExpect(
        jsonPath("$[0].employeeId").value(testAttendanceInfo.getEmployeeId())
      );
  }

  @Test
  void clockIn_ShouldCreateNewAttendanceRecord() throws Exception {
    when(
      attendanceService.clockIn(
        eq(testClockInData.getEmployeeId()),
        any(LocalDateTime.class),
        eq(testClockInData.getNotes())
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
      .andExpect(
        jsonPath("$.employeeId").value(testAttendanceInfo.getEmployeeId())
      );
  }

  @Test
  void clockOut_ShouldUpdateAttendanceRecord() throws Exception {
    when(
      attendanceService.clockOut(
        eq(testClockOutData.getAttendanceId()),
        any(LocalDateTime.class),
        eq(testClockOutData.getNotes())
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
      .andExpect(
        jsonPath("$.employeeId").value(testAttendanceInfo.getEmployeeId())
      );
  }

  @Test
  void findByEmployeeId_ShouldReturnEmployeeAttendanceRecords()
    throws Exception {
    when(attendanceService.findByEmployeeId(1L)).thenReturn(
      List.of(testAttendanceInfo)
    );

    mockMvc
      .perform(get("/api/attendance/employee/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testAttendanceInfo.getId()))
      .andExpect(
        jsonPath("$[0].employeeId").value(testAttendanceInfo.getEmployeeId())
      );
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
          .param("employeeId", "1")
          .param("startDate", startDate.toString())
          .param("endDate", endDate.toString())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value(testAttendanceInfo.getId()));
  }
}
