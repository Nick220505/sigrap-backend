package com.sigrap.employee.attendance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sigrap.user.User;
import com.sigrap.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
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
  private LocalDateTime testNow;
  private LocalDate testDate;

  @BeforeEach
  void setUp() {
    testNow = LocalDateTime.now().withNano(0);
    testDate = testNow.toLocalDate();

    testUser = User.builder()
      .id(1L)
      .name("John Doe")
      .email("john.doe@example.com")
      .build();

    testAttendance = Attendance.builder()
      .id(1L)
      .user(testUser)
      .date(testNow)
      .clockInTime(testNow)
      .clockOutTime(null)
      .status(AttendanceStatus.PRESENT)
      .build();

    testAttendanceInfo = AttendanceInfo.builder()
      .id(1L)
      .userId(1L)
      .userName("John Doe")
      .date(testNow)
      .clockInTime(testNow)
      .clockOutTime(testNow.plusHours(8))
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
  void findById_ShouldReturnAttendanceRecord() {
    doReturn(Optional.of(testAttendance))
      .when(attendanceRepository)
      .findById(1L);
    doReturn(testAttendanceInfo).when(attendanceMapper).toInfo(testAttendance);

    AttendanceInfo result = attendanceService.findById(1L);

    assertNotNull(result);
    assertEquals(testAttendanceInfo.getId(), result.getId());
  }

  @Test
  void findById_WithInvalidId_ShouldThrowException() {
    doReturn(Optional.empty()).when(attendanceRepository).findById(1L);

    assertThrows(EntityNotFoundException.class, () ->
      attendanceService.findById(1L)
    );
  }

  @Test
  void clockIn_ShouldCreateNewAttendanceRecord() {
    lenient()
      .when(userRepository.findById(anyLong()))
      .thenReturn(Optional.of(testUser));
    lenient()
      .when(
        attendanceRepository.findByUserIdAndDate(
          anyLong(),
          any(LocalDateTime.class)
        )
      )
      .thenReturn(Optional.empty());
    lenient().when(attendanceRepository.save(any())).thenReturn(testAttendance);
    lenient()
      .when(attendanceMapper.toInfo(any(Attendance.class)))
      .thenReturn(testAttendanceInfo);

    AttendanceInfo result = attendanceService.clockIn(1L, LocalDateTime.now());

    assertNotNull(result);
    assertEquals(testAttendanceInfo.getId(), result.getId());
    assertEquals(testAttendanceInfo.getUserId(), result.getUserId());
  }

  @Test
  void clockOut_ShouldUpdateAttendanceRecord() {
    lenient()
      .when(attendanceRepository.findById(anyLong()))
      .thenReturn(Optional.of(testAttendance));
    lenient()
      .when(attendanceRepository.save(any(Attendance.class)))
      .thenReturn(testAttendance);
    lenient()
      .when(attendanceMapper.toInfo(any(Attendance.class)))
      .thenReturn(testAttendanceInfo);

    AttendanceInfo result = attendanceService.clockOut(1L, LocalDateTime.now());

    assertNotNull(result);
    assertEquals(testAttendanceInfo.getId(), result.getId());
    assertEquals(testAttendanceInfo.getUserId(), result.getUserId());
  }

  @Test
  void findByUserId_ShouldReturnUserAttendanceRecords() {
    List<Attendance> attendances = List.of(testAttendance);
    lenient()
      .when(attendanceRepository.findByUserId(anyLong()))
      .thenReturn(attendances);
    lenient()
      .when(attendanceMapper.toInfoList(attendances))
      .thenReturn(List.of(testAttendanceInfo));

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

    lenient()
      .when(
        attendanceRepository.findByUserIdAndDateBetween(
          anyLong(),
          any(LocalDateTime.class),
          any(LocalDateTime.class)
        )
      )
      .thenReturn(attendances);
    lenient()
      .when(attendanceMapper.toInfoList(attendances))
      .thenReturn(List.of(testAttendanceInfo));

    List<AttendanceInfo> result = attendanceService.generateAttendanceReport(
      1L,
      startDate,
      endDate
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendanceInfo.getId(), result.get(0).getId());
  }

  @Test
  void updateStatus_ShouldUpdateAttendanceStatus() {
    Long attendanceId = 1L;
    AttendanceStatus newStatus = AttendanceStatus.ON_LEAVE;

    when(attendanceRepository.findById(attendanceId)).thenReturn(
      Optional.of(testAttendance)
    );
    when(attendanceRepository.save(testAttendance)).thenReturn(testAttendance);
    when(attendanceMapper.toInfo(testAttendance)).thenReturn(
      testAttendanceInfo
    );

    AttendanceInfo result = attendanceService.updateStatus(
      attendanceId,
      newStatus
    );

    assertNotNull(result);
    assertEquals(testAttendanceInfo.getId(), result.getId());
    verify(attendanceRepository).findById(attendanceId);
    verify(attendanceRepository).save(testAttendance);
  }

  @Test
  void updateStatus_WithInvalidId_ShouldThrowException() {
    Long attendanceId = 1L;
    AttendanceStatus newStatus = AttendanceStatus.ON_LEAVE;

    when(attendanceRepository.findById(attendanceId)).thenReturn(
      Optional.empty()
    );

    assertThrows(EntityNotFoundException.class, () ->
      attendanceService.updateStatus(attendanceId, newStatus)
    );
  }

  @Test
  void findByDate_ShouldReturnAttendanceRecordsForDate() {
    LocalDate date = testDate;
    List<Attendance> attendances = List.of(testAttendance);
    List<AttendanceInfo> expectedInfos = List.of(testAttendanceInfo);

    lenient()
      .when(attendanceRepository.findByDate(any(LocalDateTime.class)))
      .thenReturn(attendances);
    lenient()
      .when(attendanceMapper.toInfoList(any()))
      .thenReturn(expectedInfos);

    List<AttendanceInfo> result = attendanceService.findByDate(date);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendanceInfo.getId(), result.get(0).getId());
  }

  @Test
  void findByStatus_ShouldReturnAttendanceRecordsWithStatus() {
    AttendanceStatus status = AttendanceStatus.PRESENT;
    List<Attendance> attendances = List.of(testAttendance);
    List<AttendanceInfo> expectedInfos = List.of(testAttendanceInfo);

    lenient()
      .when(attendanceRepository.findByStatus(status))
      .thenReturn(attendances);
    lenient()
      .when(attendanceMapper.toInfoList(any()))
      .thenReturn(expectedInfos);

    List<AttendanceInfo> result = attendanceService.findByStatus(status);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendanceInfo.getId(), result.get(0).getId());
  }

  @Test
  void findByDateRange_ShouldReturnAttendanceRecordsInRange() {
    LocalDateTime startDate = testNow.minusDays(7);
    LocalDateTime endDate = testNow;
    List<Attendance> attendances = List.of(testAttendance);
    List<AttendanceInfo> expectedInfos = List.of(testAttendanceInfo);

    lenient()
      .when(attendanceRepository.findByDateBetween(any(), any()))
      .thenReturn(attendances);
    lenient()
      .when(attendanceMapper.toInfoList(any()))
      .thenReturn(expectedInfos);

    List<AttendanceInfo> result = attendanceService.findByDateRange(
      startDate,
      endDate
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendanceInfo.getId(), result.get(0).getId());
  }
}
