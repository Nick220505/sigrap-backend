package com.sigrap.employee;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class AttendanceRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private AttendanceRepository attendanceRepository;

  private Employee testEmployee;
  private Attendance testAttendance;

  @BeforeEach
  void setUp() {
    testEmployee = Employee.builder()
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .position("Sales")
      .department("Sales")
      .hireDate(LocalDateTime.now())
      .build();
    entityManager.persist(testEmployee);

    testAttendance = Attendance.builder()
      .employee(testEmployee)
      .date(LocalDateTime.now())
      .clockInTime(LocalDateTime.now())
      .clockOutTime(LocalDateTime.now().plusHours(8))
      .totalHours(8.0)
      .status(Attendance.AttendanceStatus.PRESENT)
      .notes("Regular day")
      .build();
    entityManager.persist(testAttendance);
    entityManager.flush();
  }

  @Test
  void findByEmployeeId_ShouldReturnAttendanceRecords() {
    List<Attendance> result = attendanceRepository.findByEmployeeId(
      testEmployee.getId()
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendance.getId(), result.get(0).getId());
  }

  @Test
  void findByEmployeeIdAndDateBetween_ShouldReturnAttendanceRecords() {
    LocalDateTime startDate = LocalDateTime.now().minusDays(1);
    LocalDateTime endDate = LocalDateTime.now().plusDays(1);

    List<Attendance> result =
      attendanceRepository.findByEmployeeIdAndDateBetween(
        testEmployee.getId(),
        startDate,
        endDate
      );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendance.getId(), result.get(0).getId());
  }

  @Test
  void findByDate_ShouldReturnAttendanceRecords() {
    List<Attendance> result = attendanceRepository.findByDate(
      testAttendance.getDate()
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendance.getId(), result.get(0).getId());
  }

  @Test
  void findByStatus_ShouldReturnAttendanceRecords() {
    List<Attendance> result = attendanceRepository.findByStatus(
      Attendance.AttendanceStatus.PRESENT
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendance.getId(), result.get(0).getId());
  }

  @Test
  void findByEmployeeIdAndDate_ShouldReturnAttendanceRecord() {
    Optional<Attendance> result = attendanceRepository.findByEmployeeIdAndDate(
      testEmployee.getId(),
      testAttendance.getDate()
    );

    assertTrue(result.isPresent());
    assertEquals(testAttendance.getId(), result.get().getId());
  }

  @Test
  void findByEmployeeIdAndStatus_ShouldReturnAttendanceRecords() {
    List<Attendance> result = attendanceRepository.findByEmployeeIdAndStatus(
      testEmployee.getId(),
      Attendance.AttendanceStatus.PRESENT
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendance.getId(), result.get(0).getId());
  }
}
