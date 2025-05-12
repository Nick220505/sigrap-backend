package com.sigrap.employee;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

  @Mock
  private AttendanceRepository attendanceRepository;

  @Mock
  private AttendanceMapper attendanceMapper;

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private AttendanceService attendanceService;

  private Employee testEmployee;
  private Attendance testAttendance;
  private AttendanceInfo testAttendanceInfo;

  @BeforeEach
  void setUp() {
    testEmployee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .build();

    testAttendance = Attendance.builder()
      .id(1L)
      .employee(testEmployee)
      .date(LocalDateTime.now())
      .clockInTime(LocalDateTime.now())
      .clockOutTime(LocalDateTime.now().plusHours(8))
      .totalHours(8.0)
      .status(Attendance.AttendanceStatus.PRESENT)
      .notes("Regular day")
      .build();

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
  }

  @Test
  void findAll_ShouldReturnAllAttendanceRecords() {
    when(attendanceRepository.findAll()).thenReturn(List.of(testAttendance));
    when(attendanceMapper.toInfo(any(Attendance.class))).thenReturn(
      testAttendanceInfo
    );

    List<AttendanceInfo> result = attendanceService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendanceInfo.getId(), result.get(0).getId());
  }

  @Test
  void clockIn_ShouldCreateNewAttendanceRecord() {
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
    when(attendanceRepository.save(any(Attendance.class))).thenReturn(
      testAttendance
    );
    when(attendanceMapper.toInfo(any(Attendance.class))).thenReturn(
      testAttendanceInfo
    );

    AttendanceInfo result = attendanceService.clockIn(
      1L,
      LocalDateTime.now(),
      "On time"
    );

    assertNotNull(result);
    assertEquals(testAttendanceInfo.getId(), result.getId());
    assertEquals(testAttendanceInfo.getEmployeeId(), result.getEmployeeId());
  }

  @Test
  void clockOut_ShouldUpdateAttendanceRecord() {
    when(attendanceRepository.findById(1L)).thenReturn(
      Optional.of(testAttendance)
    );
    when(attendanceRepository.save(any(Attendance.class))).thenReturn(
      testAttendance
    );
    when(attendanceMapper.toInfo(any(Attendance.class))).thenReturn(
      testAttendanceInfo
    );

    AttendanceInfo result = attendanceService.clockOut(
      1L,
      LocalDateTime.now(),
      "Regular end of shift"
    );

    assertNotNull(result);
    assertEquals(testAttendanceInfo.getId(), result.getId());
    assertEquals(testAttendanceInfo.getEmployeeId(), result.getEmployeeId());
  }

  @Test
  void findByEmployeeId_ShouldReturnEmployeeAttendanceRecords() {
    when(attendanceRepository.findByEmployeeId(1L)).thenReturn(
      List.of(testAttendance)
    );
    when(attendanceMapper.toInfo(any(Attendance.class))).thenReturn(
      testAttendanceInfo
    );

    List<AttendanceInfo> result = attendanceService.findByEmployeeId(1L);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendanceInfo.getId(), result.get(0).getId());
  }

  @Test
  void generateAttendanceReport_ShouldReturnReport() {
    LocalDateTime startDate = LocalDateTime.now().minusDays(7);
    LocalDateTime endDate = LocalDateTime.now();

    when(
      attendanceRepository.findByEmployeeIdAndDateBetween(
        1L,
        startDate,
        endDate
      )
    ).thenReturn(List.of(testAttendance));
    when(attendanceMapper.toInfo(any(Attendance.class))).thenReturn(
      testAttendanceInfo
    );

    List<AttendanceInfo> result = attendanceService.generateAttendanceReport(
      1L,
      startDate,
      endDate
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendanceInfo.getId(), result.get(0).getId());
  }
}
