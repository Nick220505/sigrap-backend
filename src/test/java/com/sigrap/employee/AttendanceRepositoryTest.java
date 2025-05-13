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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.sigrap.config.RepositoryTestConfiguration;
import com.sigrap.user.User;

@DataJpaTest
@ActiveProfiles("test")
@Import(RepositoryTestConfiguration.class)
class AttendanceRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private AttendanceRepository attendanceRepository;

  private Employee testEmployee;
  private Attendance testAttendance;

  @BeforeEach
  void setUp() {
    // Generate unique IDs for the test
    String uniqueEmail = "john" + System.currentTimeMillis() + "@example.com";
    String uniqueDocumentId = "DOC" + System.currentTimeMillis();

    // Create a user for the employee
    User user = User.builder()
      .name("John Doe")
      .email(uniqueEmail)
      .password("password")
      .status(User.UserStatus.ACTIVE)
      .build();
    entityManager.persist(user);

    testEmployee = Employee.builder()
      .firstName("John")
      .lastName("Doe")
      .documentId(uniqueDocumentId)
      .position("Sales")
      .department("Sales")
      .hireDate(LocalDateTime.now())
      .user(user)
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
    // Get the date part only without the time
    LocalDateTime startOfDay = testAttendance
      .getDate()
      .toLocalDate()
      .atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

    List<Attendance> result = attendanceRepository.findByDateBetween(
      startOfDay,
      endOfDay
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
    // Create a specific date time to use for both the entity and the query
    LocalDateTime specificDateTime = LocalDateTime.now().withNano(0);

    // Update the saved entity with this specific date time
    testAttendance.setDate(specificDateTime);
    testAttendance = entityManager.merge(testAttendance);
    entityManager.flush();

    Optional<Attendance> result = attendanceRepository.findByEmployeeIdAndDate(
      testEmployee.getId(),
      specificDateTime
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
