package com.sigrap.employee.attendance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
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
class AttendanceServiceTest {

  @Mock
  private AttendanceRepository attendanceRepository;

  @Mock
  private AttendanceMapper attendanceMapper;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AttendanceService attendanceService;

  private User testUser;
  private Attendance testAttendance;
  private AttendanceInfo testAttendanceInfo;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
      .id(1L)
      .name("John Doe")
      .email("john.doe@example.com")
      .build();

    testAttendance = Attendance.builder()
      .id(1L)
      .user(testUser)
      .date(LocalDateTime.now())
      .clockInTime(LocalDateTime.now())
      .clockOutTime(null)
      .status(AttendanceStatus.PRESENT)
      .build();

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
  }

  @Test
  void findAll_ShouldReturnAllAttendanceRecords() {
    List<Attendance> attendances = List.of(testAttendance);
    doReturn(attendances).when(attendanceRepository).findAll();
    doReturn(List.of(testAttendanceInfo))
      .when(attendanceMapper)
      .toInfoList(attendances);

    List<AttendanceInfo> result = attendanceService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendanceInfo.getId(), result.get(0).getId());
  }

  @Test
  void clockIn_ShouldCreateNewAttendanceRecord() {
    doReturn(Optional.of(testUser)).when(userRepository).findById(anyLong());
    doReturn(Optional.empty())
      .when(attendanceRepository)
      .findByUserIdAndDate(anyLong(), any(LocalDateTime.class));
    doReturn(testAttendance).when(attendanceRepository).save(any());
    doReturn(testAttendanceInfo)
      .when(attendanceMapper)
      .toInfo(any(Attendance.class));

    AttendanceInfo result = attendanceService.clockIn(1L, LocalDateTime.now());

    assertNotNull(result);
    assertEquals(testAttendanceInfo.getId(), result.getId());
    assertEquals(testAttendanceInfo.getUserId(), result.getUserId());
  }

  @Test
  void clockOut_ShouldUpdateAttendanceRecord() {
    doReturn(Optional.of(testAttendance))
      .when(attendanceRepository)
      .findById(anyLong());
    doReturn(testAttendance)
      .when(attendanceRepository)
      .save(any(Attendance.class));
    doReturn(testAttendanceInfo)
      .when(attendanceMapper)
      .toInfo(any(Attendance.class));

    AttendanceInfo result = attendanceService.clockOut(1L, LocalDateTime.now());

    assertNotNull(result);
    assertEquals(testAttendanceInfo.getId(), result.getId());
    assertEquals(testAttendanceInfo.getUserId(), result.getUserId());
  }

  @Test
  void findByUserId_ShouldReturnUserAttendanceRecords() {
    List<Attendance> attendances = List.of(testAttendance);
    doReturn(attendances).when(attendanceRepository).findByUserId(anyLong());
    doReturn(List.of(testAttendanceInfo))
      .when(attendanceMapper)
      .toInfoList(attendances);

    List<AttendanceInfo> result = attendanceService.findByUserId(1L);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendanceInfo.getId(), result.get(0).getId());
  }

  @Test
  void generateAttendanceReport_ShouldReturnReport() {
    LocalDateTime startDate = LocalDateTime.now().minusDays(7);
    LocalDateTime endDate = LocalDateTime.now();
    List<Attendance> attendances = List.of(testAttendance);

    doReturn(attendances)
      .when(attendanceRepository)
      .findByUserIdAndDateBetween(
        anyLong(),
        any(LocalDateTime.class),
        any(LocalDateTime.class)
      );
    doReturn(List.of(testAttendanceInfo))
      .when(attendanceMapper)
      .toInfoList(attendances);

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
