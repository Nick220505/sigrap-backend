package com.sigrap.employee.attendance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

import com.sigrap.employee.Employee;
import com.sigrap.employee.EmployeeRepository;
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
      .clockOutTime(null)
      .status(AttendanceStatus.PRESENT)
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
      .status(AttendanceStatus.PRESENT)
      .notes("Regular day")
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
    doReturn(Optional.of(testEmployee))
      .when(employeeRepository)
      .findById(anyLong());
    doReturn(Optional.empty())
      .when(attendanceRepository)
      .findByEmployeeIdAndDate(anyLong(), any());
    doReturn(testAttendance).when(attendanceRepository).save(any());
    doReturn(testAttendanceInfo).when(attendanceMapper).toInfo(any());

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
    doReturn(Optional.of(testAttendance))
      .when(attendanceRepository)
      .findById(anyLong());
    doReturn(testAttendance).when(attendanceRepository).save(any());
    doReturn(testAttendanceInfo).when(attendanceMapper).toInfo(any());

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
    List<Attendance> attendances = List.of(testAttendance);
    doReturn(attendances)
      .when(attendanceRepository)
      .findByEmployeeId(anyLong());
    doReturn(List.of(testAttendanceInfo))
      .when(attendanceMapper)
      .toInfoList(attendances);

    List<AttendanceInfo> result = attendanceService.findByEmployeeId(1L);

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
      .findByEmployeeIdAndDateBetween(
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
