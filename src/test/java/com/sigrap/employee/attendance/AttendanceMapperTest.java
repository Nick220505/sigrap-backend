package com.sigrap.employee.attendance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AttendanceMapperTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AttendanceMapper attendanceMapper;

  private User testUser;
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

    testUser = User.builder()
      .id(1L)
      .name("John Doe")
      .email("john.doe@example.com")
      .build();

    testAttendance = Attendance.builder()
      .id(1L)
      .user(testUser)
      .date(date)
      .clockInTime(clockInTime)
      .clockOutTime(clockOutTime)
      .totalHours(8.0)
      .status(AttendanceStatus.PRESENT)
      .build();

    testAttendanceInfo = AttendanceInfo.builder()
      .id(1L)
      .userId(1L)
      .userName("John Doe")
      .date(date)
      .clockInTime(clockInTime)
      .clockOutTime(clockOutTime)
      .totalHours(8.0)
      .status(AttendanceStatus.PRESENT)
      .build();

    testAttendanceData = AttendanceData.builder()
      .userId(1L)
      .date(date)
      .clockInTime(clockInTime)
      .clockOutTime(clockOutTime)
      .status(AttendanceStatus.PRESENT)
      .build();
  }

  @Test
  void toInfo_ShouldMapEntityToInfo() {
    AttendanceInfo result = attendanceMapper.toInfo(testAttendance);

    assertNotNull(result);
    assertEquals(testAttendance.getId(), result.getId());
    assertEquals(testAttendance.getUser().getId(), result.getUserId());
    assertEquals("John Doe", result.getUserName());
    assertEquals(testAttendance.getDate(), result.getDate());
    assertEquals(testAttendance.getClockInTime(), result.getClockInTime());
    assertEquals(testAttendance.getClockOutTime(), result.getClockOutTime());
    assertEquals(testAttendance.getTotalHours(), result.getTotalHours());
    assertEquals(testAttendance.getStatus(), result.getStatus());
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
    assertEquals(testAttendance.getUser().getId(), result.get(0).getUserId());
    assertEquals("John Doe", result.get(0).getUserName());
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
  }

  @Test
  void toInfoList_ShouldReturnEmptyList_WhenEntitiesIsNull() {
    List<AttendanceInfo> result = attendanceMapper.toInfoList(null);

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  void toEntity_ShouldMapDataToEntity() {
    when(userRepository.findById(testAttendanceData.getUserId())).thenReturn(
      Optional.of(testUser)
    );

    Attendance result = attendanceMapper.toEntity(testAttendanceData);

    assertNotNull(result);
    assertEquals(testUser, result.getUser());
    assertEquals(testAttendanceData.getDate(), result.getDate());
    assertEquals(testAttendanceData.getClockInTime(), result.getClockInTime());
    assertEquals(
      testAttendanceData.getClockOutTime(),
      result.getClockOutTime()
    );
    assertEquals(8.0, result.getTotalHours());
    assertEquals(testAttendanceData.getStatus(), result.getStatus());
  }

  @Test
  void toEntity_ShouldThrowEntityNotFoundException_WhenEmployeeNotFound() {
    when(userRepository.findById(testAttendanceData.getUserId())).thenReturn(
      Optional.empty()
    );

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
      .user(testUser)
      .date(date.minusDays(1))
      .clockInTime(clockInTime.minusDays(1))
      .clockOutTime(clockOutTime.minusDays(1))
      .totalHours(7.0)
      .status(AttendanceStatus.LATE)
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
  }

  @Test
  void updateEntityFromData_ShouldNotUpdateEmployee_WhenDataIsNull() {
    Attendance existingAttendance = Attendance.builder()
      .id(1L)
      .user(testUser)
      .date(date.minusDays(1))
      .clockInTime(clockInTime.minusDays(1))
      .clockOutTime(clockOutTime.minusDays(1))
      .totalHours(7.0)
      .status(AttendanceStatus.LATE)
      .build();

    attendanceMapper.updateEntityFromData(existingAttendance, null);

    assertEquals(testUser, existingAttendance.getUser());
    assertEquals(date.minusDays(1), existingAttendance.getDate());
    assertEquals(clockInTime.minusDays(1), existingAttendance.getClockInTime());
    assertEquals(
      clockOutTime.minusDays(1),
      existingAttendance.getClockOutTime()
    );
    assertEquals(7.0, existingAttendance.getTotalHours());
    assertEquals(AttendanceStatus.LATE, existingAttendance.getStatus());
  }
}
