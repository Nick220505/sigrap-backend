package com.sigrap.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttendanceMapperTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private AttendanceMapper attendanceMapper;

  private Employee testEmployee;
  private Attendance testAttendance;
  private AttendanceInfo testAttendanceInfo;
  private AttendanceData testAttendanceData;
  private LocalDateTime date;
  private LocalDateTime clockInTime;
  private LocalDateTime clockOutTime;

  @BeforeEach
  void setUp() {
    date = LocalDateTime.now();
    clockInTime = date.withHour(9).withMinute(0).withSecond(0).withNano(0);
    clockOutTime = clockInTime.plusHours(8);

    testEmployee = Employee.builder()
      .id(1L)
      .firstName("John")
      .lastName("Doe")
      .build();

    testAttendance = Attendance.builder()
      .id(1L)
      .employee(testEmployee)
      .date(date)
      .clockInTime(clockInTime)
      .clockOutTime(clockOutTime)
      .totalHours(8.0)
      .status(Attendance.AttendanceStatus.PRESENT)
      .notes("Regular day")
      .build();

    testAttendanceInfo = AttendanceInfo.builder()
      .id(1L)
      .employeeId(1L)
      .employeeName("John Doe")
      .date(date)
      .clockInTime(clockInTime)
      .clockOutTime(clockOutTime)
      .totalHours(8.0)
      .status(Attendance.AttendanceStatus.PRESENT)
      .notes("Regular day")
      .build();

    testAttendanceData = AttendanceData.builder()
      .employeeId(1L)
      .date(date)
      .clockInTime(clockInTime)
      .clockOutTime(clockOutTime)
      .status(Attendance.AttendanceStatus.PRESENT)
      .notes("Regular day")
      .build();
  }

  @Test
  void toInfo_ShouldMapEntityToInfo() {
    AttendanceInfo result = attendanceMapper.toInfo(testAttendance);

    assertNotNull(result);
    assertEquals(testAttendance.getId(), result.getId());
    assertEquals(testAttendance.getEmployee().getId(), result.getEmployeeId());
    assertEquals("John Doe", result.getEmployeeName());
    assertEquals(testAttendance.getDate(), result.getDate());
    assertEquals(testAttendance.getClockInTime(), result.getClockInTime());
    assertEquals(testAttendance.getClockOutTime(), result.getClockOutTime());
    assertEquals(testAttendance.getTotalHours(), result.getTotalHours());
    assertEquals(testAttendance.getStatus(), result.getStatus());
    assertEquals(testAttendance.getNotes(), result.getNotes());
  }

  @Test
  void toInfo_ShouldReturnNull_WhenEntityIsNull() {
    AttendanceInfo result = attendanceMapper.toInfo(null);

    assertEquals(null, result);
  }

  @Test
  void toInfoList_ShouldMapEntitiesToInfos() {
    List<AttendanceInfo> result = attendanceMapper.toInfoList(
      List.of(testAttendance)
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendance.getId(), result.get(0).getId());
    assertEquals(
      testAttendance.getEmployee().getId(),
      result.get(0).getEmployeeId()
    );
    assertEquals("John Doe", result.get(0).getEmployeeName());
    assertEquals(testAttendance.getDate(), result.get(0).getDate());
    assertEquals(
      testAttendance.getClockInTime(),
      result.get(0).getClockInTime()
    );
    assertEquals(
      testAttendance.getClockOutTime(),
      result.get(0).getClockOutTime()
    );
    assertEquals(testAttendance.getTotalHours(), result.get(0).getTotalHours());
    assertEquals(testAttendance.getStatus(), result.get(0).getStatus());
    assertEquals(testAttendance.getNotes(), result.get(0).getNotes());
  }

  @Test
  void toInfoList_ShouldReturnEmptyList_WhenEntitiesIsNull() {
    List<AttendanceInfo> result = attendanceMapper.toInfoList(null);

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  void toEntity_ShouldMapDataToEntity() {
    when(
      employeeRepository.findById(testAttendanceData.getEmployeeId())
    ).thenReturn(Optional.of(testEmployee));

    Attendance result = attendanceMapper.toEntity(testAttendanceData);

    assertNotNull(result);
    assertEquals(testEmployee, result.getEmployee());
    assertEquals(testAttendanceData.getDate(), result.getDate());
    assertEquals(testAttendanceData.getClockInTime(), result.getClockInTime());
    assertEquals(
      testAttendanceData.getClockOutTime(),
      result.getClockOutTime()
    );
    assertEquals(8.0, result.getTotalHours());
    assertEquals(testAttendanceData.getStatus(), result.getStatus());
    assertEquals(testAttendanceData.getNotes(), result.getNotes());
  }

  @Test
  void toEntity_ShouldThrowEntityNotFoundException_WhenEmployeeNotFound() {
    when(
      employeeRepository.findById(testAttendanceData.getEmployeeId())
    ).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () ->
      attendanceMapper.toEntity(testAttendanceData)
    );
  }

  @Test
  void toEntity_ShouldReturnNull_WhenDataIsNull() {
    Attendance result = attendanceMapper.toEntity(null);

    assertEquals(null, result);
  }

  @Test
  void updateEntityFromData_ShouldUpdateEntityWithData() {
    Attendance existingAttendance = Attendance.builder()
      .id(1L)
      .employee(testEmployee)
      .date(date.minusDays(1))
      .clockInTime(clockInTime.minusDays(1))
      .clockOutTime(clockOutTime.minusDays(1))
      .totalHours(7.0)
      .status(Attendance.AttendanceStatus.LATE)
      .notes("Old attendance")
      .build();

    attendanceMapper.updateEntityFromData(
      existingAttendance,
      testAttendanceData
    );

    assertEquals(testAttendanceData.getDate(), existingAttendance.getDate());
    assertEquals(
      testAttendanceData.getClockInTime(),
      existingAttendance.getClockInTime()
    );
    assertEquals(
      testAttendanceData.getClockOutTime(),
      existingAttendance.getClockOutTime()
    );
    assertEquals(8.0, existingAttendance.getTotalHours());
    assertEquals(
      testAttendanceData.getStatus(),
      existingAttendance.getStatus()
    );
    assertEquals(testAttendanceData.getNotes(), existingAttendance.getNotes());
  }

  @Test
  void updateEntityFromData_ShouldNotUpdateEmployee_WhenDataIsNull() {
    Attendance existingAttendance = Attendance.builder()
      .id(1L)
      .employee(testEmployee)
      .date(date.minusDays(1))
      .clockInTime(clockInTime.minusDays(1))
      .clockOutTime(clockOutTime.minusDays(1))
      .totalHours(7.0)
      .status(Attendance.AttendanceStatus.LATE)
      .notes("Old attendance")
      .build();

    attendanceMapper.updateEntityFromData(existingAttendance, null);

    assertEquals(testEmployee, existingAttendance.getEmployee());
    assertEquals(date.minusDays(1), existingAttendance.getDate());
    assertEquals(clockInTime.minusDays(1), existingAttendance.getClockInTime());
    assertEquals(
      clockOutTime.minusDays(1),
      existingAttendance.getClockOutTime()
    );
    assertEquals(7.0, existingAttendance.getTotalHours());
    assertEquals(
      Attendance.AttendanceStatus.LATE,
      existingAttendance.getStatus()
    );
    assertEquals("Old attendance", existingAttendance.getNotes());
  }
}
