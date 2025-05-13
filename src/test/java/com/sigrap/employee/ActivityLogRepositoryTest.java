package com.sigrap.employee;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.sigrap.config.RepositoryTestConfiguration;
import com.sigrap.user.User;

/**
 * Integration tests for the ActivityLogRepository class.
 * Tests the JPA repository functionality with a real database.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(RepositoryTestConfiguration.class)
class ActivityLogRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ActivityLogRepository activityLogRepository;

  private Employee employee;
  private ActivityLog activityLog1;
  private ActivityLog activityLog2;
  private LocalDateTime timestamp;

  @BeforeEach
  void setUp() {
    timestamp = LocalDateTime.now();

    // Create a user first
    User user = User.builder()
      .name("John Doe")
      .email("john@example.com")
      .password("password")
      .status(User.UserStatus.ACTIVE)
      .build();
    user = entityManager.persist(user);

    employee = Employee.builder()
      .firstName("John")
      .lastName("Doe")
      .email("john@example.com")
      .documentId("12345678")
      .department("Sales")
      .position("Manager")
      .hireDate(LocalDateTime.now().minusMonths(1))
      .user(user)
      .build();
    employee = entityManager.persist(employee);

    activityLog1 = ActivityLog.builder()
      .employee(employee)
      .timestamp(timestamp)
      .actionType(ActivityLog.ActionType.CREATE)
      .description("Test activity 1")
      .moduleName("test")
      .entityId("123")
      .ipAddress("127.0.0.1")
      .build();
    activityLog1 = entityManager.persist(activityLog1);

    activityLog2 = ActivityLog.builder()
      .employee(employee)
      .timestamp(timestamp.plusHours(1))
      .actionType(ActivityLog.ActionType.UPDATE)
      .description("Test activity 2")
      .moduleName("other")
      .entityId("456")
      .ipAddress("127.0.0.1")
      .build();
    activityLog2 = entityManager.persist(activityLog2);

    entityManager.flush();
  }

  @Test
  void findByEmployeeId_shouldReturnActivityLogs() {
    List<ActivityLog> result = activityLogRepository.findByEmployeeId(
      employee.getId()
    );

    assertThat(result)
      .hasSize(2)
      .containsExactlyInAnyOrder(activityLog1, activityLog2);
  }

  @Test
  void findByEmployeeIdAndTimestampBetween_shouldReturnActivityLogs() {
    LocalDateTime startDate = timestamp.minusHours(1);
    LocalDateTime endDate = timestamp.plusHours(2);

    List<ActivityLog> result =
      activityLogRepository.findByEmployeeIdAndTimestampBetween(
        employee.getId(),
        startDate,
        endDate
      );

    assertThat(result)
      .hasSize(2)
      .containsExactlyInAnyOrder(activityLog1, activityLog2);
  }

  @Test
  void findByActionType_shouldReturnActivityLogs() {
    List<ActivityLog> result = activityLogRepository.findByActionType(
      ActivityLog.ActionType.CREATE
    );

    assertThat(result).hasSize(1).containsExactly(activityLog1);
  }

  @Test
  void findByModuleName_shouldReturnActivityLogs() {
    List<ActivityLog> result = activityLogRepository.findByModuleName("test");

    assertThat(result).hasSize(1).containsExactly(activityLog1);
  }

  @Test
  void findByTimestampBetween_shouldReturnActivityLogs() {
    LocalDateTime startDate = timestamp.minusHours(1);
    LocalDateTime endDate = timestamp.plusHours(2);

    List<ActivityLog> result = activityLogRepository.findByTimestampBetween(
      startDate,
      endDate
    );

    assertThat(result)
      .hasSize(2)
      .containsExactlyInAnyOrder(activityLog1, activityLog2);
  }

  @Test
  void findByTimestampBetween_withNoMatchingDates_shouldReturnEmptyList() {
    LocalDateTime startDate = timestamp.plusDays(1);
    LocalDateTime endDate = timestamp.plusDays(2);

    List<ActivityLog> result = activityLogRepository.findByTimestampBetween(
      startDate,
      endDate
    );

    assertThat(result).isEmpty();
  }
}
