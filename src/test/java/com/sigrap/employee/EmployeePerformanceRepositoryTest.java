package com.sigrap.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.sigrap.user.User;
import com.sigrap.user.User.UserStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class EmployeePerformanceRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private EmployeePerformanceRepository performanceRepository;

  private Employee testEmployee;
  private EmployeePerformance testPerformance;
  private LocalDateTime periodStart;
  private LocalDateTime periodEnd;

  @BeforeEach
  void setUp() {
    User user = User.builder()
      .name("John Doe")
      .email("john.doe@example.com")
      .password("password123")
      .status(UserStatus.ACTIVE)
      .build();
    entityManager.persist(user);

    testEmployee = Employee.builder()
      .firstName("John")
      .lastName("Doe")
      .documentId("123456")
      .email("john.doe@example.com")
      .position("Sales")
      .department("Sales")
      .hireDate(LocalDateTime.now())
      .status(Employee.EmployeeStatus.ACTIVE)
      .user(user)
      .build();
    entityManager.persist(testEmployee);

    periodStart = LocalDateTime.now().minusDays(30);
    periodEnd = LocalDateTime.now();

    testPerformance = EmployeePerformance.builder()
      .employee(testEmployee)
      .periodStart(periodStart)
      .periodEnd(periodEnd)
      .salesCount(100)
      .salesTotal(BigDecimal.valueOf(10000))
      .transactionAverage(BigDecimal.valueOf(100))
      .rating(90)
      .notes("Great performance")
      .build();
    entityManager.persist(testPerformance);
    entityManager.flush();
  }

  @Test
  void findByEmployeeId_ShouldReturnPerformanceRecords() {
    List<EmployeePerformance> result = performanceRepository.findByEmployeeId(
      testEmployee.getId()
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testPerformance.getId(), result.get(0).getId());
  }

  @Test
  void findByEmployeeIdAndPeriodStartBetween_ShouldReturnPerformanceRecords() {
    List<EmployeePerformance> result =
      performanceRepository.findByEmployeeIdAndPeriodStartBetween(
        testEmployee.getId(),
        periodStart.minusDays(1),
        periodEnd.plusDays(1)
      );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testPerformance.getId(), result.get(0).getId());
  }

  @Test
  void findTopPerformers_ShouldReturnTopPerformers() {
    List<EmployeePerformance> result = performanceRepository.findTopPerformers(
      periodStart.minusDays(1),
      periodEnd.plusDays(1),
      10
    );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testPerformance.getId(), result.get(0).getId());
  }

  @Test
  void findByPeriodAndMinimumRating_ShouldReturnPerformanceRecords() {
    List<EmployeePerformance> result =
      performanceRepository.findByPeriodAndMinimumRating(
        periodStart.minusDays(1),
        periodEnd.plusDays(1),
        80
      );

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testPerformance.getId(), result.get(0).getId());
  }

  @Test
  void calculateTotalSales_ShouldReturnTotalSales() {
    BigDecimal result = performanceRepository.calculateTotalSales(
      testEmployee.getId(),
      periodStart.minusDays(1),
      periodEnd.plusDays(1)
    );

    assertNotNull(result);
    assertThat(result).isEqualByComparingTo(BigDecimal.valueOf(10000));
  }
}
