package com.sigrap.employee.attendance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sigrap.config.RepositoryTestConfiguration;
import com.sigrap.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(RepositoryTestConfiguration.class)
class AttendanceRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private AttendanceRepository attendanceRepository;

  private User testUser;
  private Attendance testAttendance;

  @BeforeEach
  void setUp() {
    String uniqueEmail = "john" + System.currentTimeMillis() + "@example.com";

    testUser = User.builder()
      .name("John Doe")
      .email(uniqueEmail)
      .password("password")
      .documentId("DOC" + System.currentTimeMillis())
      .build();
    testUser = entityManager.persist(testUser);

    testAttendance = Attendance.builder()
      .user(testUser)
      .date(LocalDateTime.now())
      .clockInTime(LocalDateTime.now())
      .clockOutTime(LocalDateTime.now().plusHours(8))
      .totalHours(8.0)
      .status(AttendanceStatus.PRESENT)
      .build();
    entityManager.persist(testAttendance);
    entityManager.flush();
  }

  @Test
  void findByUserId_ShouldReturnAttendanceRecords() {
    List<Attendance> result = attendanceRepository.findByUserId(
      testUser.getId()
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendance.getId(), result.get(0).getId());
  }

  @Test
  void findByUserIdAndDateBetween_ShouldReturnAttendanceRecords() {
    LocalDateTime startDate = LocalDateTime.now().minusDays(1);
    LocalDateTime endDate = LocalDateTime.now().plusDays(1);

    List<Attendance> result = attendanceRepository.findByUserIdAndDateBetween(
      testUser.getId(),
      startDate,
      endDate
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendance.getId(), result.get(0).getId());
  }

  @Test
  void findByDate_ShouldReturnAttendanceRecords() {
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
      AttendanceStatus.PRESENT
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendance.getId(), result.get(0).getId());
  }

  @Test
  void findByUserIdAndDate_ShouldReturnAttendanceRecord() {
    LocalDateTime specificDateTime = LocalDateTime.now().withNano(0);

    testAttendance.setDate(specificDateTime);
    testAttendance = entityManager.merge(testAttendance);
    entityManager.flush();

    Optional<Attendance> result = attendanceRepository.findByUserIdAndDate(
      testUser.getId(),
      specificDateTime
    );

    assertTrue(result.isPresent());
    assertEquals(testAttendance.getId(), result.get().getId());
  }

  @Test
  void findByUserIdAndStatus_ShouldReturnAttendanceRecords() {
    List<Attendance> result = attendanceRepository.findByUserIdAndStatus(
      testUser.getId(),
      AttendanceStatus.PRESENT
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testAttendance.getId(), result.get(0).getId());
  }
}
